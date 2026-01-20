package com.github.ichanzhar.rsql

import com.github.ichanzhar.rsql.utils.RsqlParserFactory
import jakarta.persistence.*
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.hibernate.cfg.AvailableSettings
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostgresIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
    }

    private lateinit var entityManagerFactory: EntityManagerFactory
    private lateinit var entityManager: EntityManager

    @BeforeAll
    fun setupDatabase() {
        postgres.start()

        val properties = mapOf(
            AvailableSettings.JAKARTA_JDBC_URL to postgres.jdbcUrl,
            AvailableSettings.JAKARTA_JDBC_USER to postgres.username,
            AvailableSettings.JAKARTA_JDBC_PASSWORD to postgres.password,
            AvailableSettings.JAKARTA_JDBC_DRIVER to "org.postgresql.Driver",
            AvailableSettings.HBM2DDL_AUTO to "create-drop",
            AvailableSettings.SHOW_SQL to "true",
            AvailableSettings.FORMAT_SQL to "true",
            AvailableSettings.DIALECT to "org.hibernate.dialect.PostgreSQLDialect"
        )

        entityManagerFactory = Persistence.createEntityManagerFactory("test-pu", properties)
        entityManager = entityManagerFactory.createEntityManager()

        // Seed test data
        seedTestData()
    }

    @AfterAll
    fun cleanup() {
        if (::entityManager.isInitialized && entityManager.isOpen) {
            entityManager.close()
        }
        if (::entityManagerFactory.isInitialized && entityManagerFactory.isOpen) {
            entityManagerFactory.close()
        }
    }

    private fun seedTestData() {
        entityManager.transaction.begin()

        // Create departments
        val engineering = IntegrationTestDepartment(name = "Engineering", code = "ENG")
        val sales = IntegrationTestDepartment(name = "Sales", code = "SALES")
        val marketing = IntegrationTestDepartment(name = "Marketing", code = "MKT")

        entityManager.persist(engineering)
        entityManager.persist(sales)
        entityManager.persist(marketing)

        // Create users
        val users = listOf(
            IntegrationTestUser(
                name = "John Doe",
                email = "john@example.com",
                age = 30,
                active = true,
                salary = BigDecimal("75000.00"),
                status = IntegrationTestStatus.ACTIVE,
                createdAt = LocalDateTime.of(2023, 1, 15, 10, 30),
                birthDate = LocalDate.of(1993, 5, 20),
                department = engineering
            ),
            IntegrationTestUser(
                name = "Jane Smith",
                email = "jane@example.com",
                age = 28,
                active = true,
                salary = BigDecimal("82000.00"),
                status = IntegrationTestStatus.ACTIVE,
                createdAt = LocalDateTime.of(2023, 2, 20, 14, 0),
                birthDate = LocalDate.of(1995, 8, 10),
                department = engineering
            ),
            IntegrationTestUser(
                name = "Bob Wilson",
                email = "bob@example.com",
                age = 45,
                active = false,
                salary = BigDecimal("95000.00"),
                status = IntegrationTestStatus.INACTIVE,
                createdAt = LocalDateTime.of(2022, 6, 1, 9, 0),
                birthDate = LocalDate.of(1978, 3, 15),
                department = sales
            ),
            IntegrationTestUser(
                name = "Alice Brown",
                email = null,
                age = 35,
                active = true,
                salary = BigDecimal("68000.00"),
                status = IntegrationTestStatus.PENDING,
                createdAt = LocalDateTime.of(2023, 3, 10, 11, 30),
                birthDate = LocalDate.of(1988, 11, 25),
                department = marketing
            ),
            IntegrationTestUser(
                name = "Charlie Davis",
                email = "charlie@example.com",
                age = 25,
                active = true,
                salary = BigDecimal("55000.00"),
                status = IntegrationTestStatus.ACTIVE,
                createdAt = LocalDateTime.of(2023, 4, 5, 8, 0),
                birthDate = LocalDate.of(1998, 7, 4),
                department = engineering
            )
        )

        users.forEach { entityManager.persist(it) }

        entityManager.transaction.commit()
    }

    private fun <T : Any> executeRsqlQuery(rsql: String, entityClass: Class<T>): List<T> {
        val parser = RsqlParserFactory.instance()
        val rootNode = parser.parse(rsql)

        val visitor = JpaRsqlVisitor<T>()
        val specification = rootNode.accept(visitor)

        val cb: CriteriaBuilder = entityManager.criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(entityClass)
        val root: Root<T> = query.from(entityClass)

        val predicate = specification.toPredicate(root, query, cb)
        query.where(predicate)

        return entityManager.createQuery(query).resultList
    }

    // EQUAL operator tests
    @Test
    fun `should find user by exact name match`() {
        val results = executeRsqlQuery("name==John Doe", IntegrationTestUser::class.java)
        assertEquals(1, results.size)
        assertEquals("John Doe", results[0].name)
    }

    @Test
    fun `should find users by boolean field`() {
        val results = executeRsqlQuery("active==true", IntegrationTestUser::class.java)
        assertEquals(4, results.size)
        assertTrue(results.all { it.active })
    }

    @Test
    fun `should find users by enum field`() {
        val results = executeRsqlQuery("status==ACTIVE", IntegrationTestUser::class.java)
        assertEquals(3, results.size)
        assertTrue(results.all { it.status == IntegrationTestStatus.ACTIVE })
    }

    // NOT_EQUAL operator tests
    @Test
    fun `should find users not equal to name`() {
        val results = executeRsqlQuery("name!=John Doe", IntegrationTestUser::class.java)
        assertEquals(4, results.size)
        assertTrue(results.none { it.name == "John Doe" })
    }

    // GREATER_THAN operator tests
    @Test
    fun `should find users with age greater than value`() {
        val results = executeRsqlQuery("age=gt=30", IntegrationTestUser::class.java)
        assertEquals(2, results.size)
        assertTrue(results.all { it.age!! > 30 })
    }

    @Test
    fun `should find users with salary greater than value`() {
        val results = executeRsqlQuery("salary=gt=70000", IntegrationTestUser::class.java)
        assertEquals(3, results.size)
        assertTrue(results.all { it.salary!! > BigDecimal("70000") })
    }

    // GREATER_THAN_OR_EQUAL operator tests
    @Test
    fun `should find users with age greater than or equal to value`() {
        val results = executeRsqlQuery("age=ge=35", IntegrationTestUser::class.java)
        assertEquals(2, results.size)
        assertTrue(results.all { it.age!! >= 35 })
    }

    // LESS_THAN operator tests
    @Test
    fun `should find users with age less than value`() {
        val results = executeRsqlQuery("age=lt=30", IntegrationTestUser::class.java)
        assertEquals(2, results.size)
        assertTrue(results.all { it.age!! < 30 })
    }

    // LESS_THAN_OR_EQUAL operator tests
    @Test
    fun `should find users with age less than or equal to value`() {
        val results = executeRsqlQuery("age=le=28", IntegrationTestUser::class.java)
        assertEquals(2, results.size)
        assertTrue(results.all { it.age!! <= 28 })
    }

    // IN operator tests
    @Test
    fun `should find users with status in list`() {
        val results = executeRsqlQuery("status=in=(ACTIVE,PENDING)", IntegrationTestUser::class.java)
        assertEquals(4, results.size)
        assertTrue(results.all { it.status in listOf(IntegrationTestStatus.ACTIVE, IntegrationTestStatus.PENDING) })
    }

    @Test
    fun `should find users with age in list`() {
        val results = executeRsqlQuery("age=in=(25,30,35)", IntegrationTestUser::class.java)
        assertEquals(3, results.size)
    }

    // NOT_IN operator tests
    @Test
    fun `should find users with status not in list`() {
        val results = executeRsqlQuery("status=out=(INACTIVE)", IntegrationTestUser::class.java)
        assertEquals(4, results.size)
        assertTrue(results.none { it.status == IntegrationTestStatus.INACTIVE })
    }

    // IS_NULL operator tests
    @Test
    fun `should find users with null email`() {
        val results = executeRsqlQuery("email=isNull=true", IntegrationTestUser::class.java)
        assertEquals(1, results.size)
        assertNull(results[0].email)
        assertEquals("Alice Brown", results[0].name)
    }

    @Test
    fun `should find users with non-null email`() {
        val results = executeRsqlQuery("email=isNull=false", IntegrationTestUser::class.java)
        assertEquals(4, results.size)
        assertTrue(results.all { it.email != null })
    }

    // LIKE pattern (wildcard) tests
    @Test
    fun `should find users with name starting with pattern`() {
        val results = executeRsqlQuery("name==J*", IntegrationTestUser::class.java)
        assertEquals(2, results.size)
        assertTrue(results.all { it.name.startsWith("J") })
    }

    @Test
    fun `should find users with name ending with pattern`() {
        val results = executeRsqlQuery("name==*Smith", IntegrationTestUser::class.java)
        assertEquals(1, results.size)
        assertEquals("Jane Smith", results[0].name)
    }

    @Test
    fun `should find users with name containing pattern`() {
        val results = executeRsqlQuery("name==*o*", IntegrationTestUser::class.java)
        assertEquals(3, results.size) // John Doe, Bob Wilson, Alice Brown
    }

    // EQUAL_CI (case-insensitive) tests
    @Test
    fun `should find users with case-insensitive name match`() {
        val results = executeRsqlQuery("name=eqci=john doe", IntegrationTestUser::class.java)
        assertEquals(1, results.size)
        assertEquals("John Doe", results[0].name)
    }

    // AND logical operator tests
    @Test
    fun `should find users matching multiple AND conditions`() {
        val results = executeRsqlQuery("active==true;age=gt=25", IntegrationTestUser::class.java)
        assertEquals(3, results.size)
        assertTrue(results.all { it.active && it.age!! > 25 })
    }

    @Test
    fun `should find users with department and status`() {
        val results = executeRsqlQuery("department.code==ENG;status==ACTIVE", IntegrationTestUser::class.java)
        assertEquals(3, results.size)
    }

    // OR logical operator tests
    @Test
    fun `should find users matching OR conditions`() {
        val results = executeRsqlQuery("name==John Doe,name==Jane Smith", IntegrationTestUser::class.java)
        assertEquals(2, results.size)
    }

    @Test
    fun `should find users with age less than 26 or greater than 40`() {
        val results = executeRsqlQuery("age=lt=26,age=gt=40", IntegrationTestUser::class.java)
        assertEquals(2, results.size) // Charlie (25) and Bob (45)
    }

    // Complex query tests
    @Test
    fun `should handle complex query with AND and OR`() {
        val results = executeRsqlQuery("(status==ACTIVE,status==PENDING);age=ge=30", IntegrationTestUser::class.java)
        assertEquals(2, results.size) // John (30, ACTIVE) and Alice (35, PENDING)
    }

    // Nested property (join) tests
    @Test
    fun `should find users by department name`() {
        val results = executeRsqlQuery("department.name==Engineering", IntegrationTestUser::class.java)
        assertEquals(3, results.size)
        assertTrue(results.all { it.department?.name == "Engineering" })
    }

    @Test
    fun `should find users by department code`() {
        val results = executeRsqlQuery("department.code==SALES", IntegrationTestUser::class.java)
        assertEquals(1, results.size)
        assertEquals("Bob Wilson", results[0].name)
    }

    // Date/Time tests
    @Test
    fun `should find users created after date`() {
        val results = executeRsqlQuery("createdAt=gt=2023-02-01T00:00:00", IntegrationTestUser::class.java)
        assertEquals(3, results.size) // Jane, Alice, Charlie
    }

    @Test
    fun `should find users by birth date`() {
        val results = executeRsqlQuery("birthDate=lt=1990-01-01", IntegrationTestUser::class.java)
        assertEquals(1, results.size)
        assertEquals("Bob Wilson", results[0].name)
    }
}

// Test entities for integration tests
enum class IntegrationTestStatus {
    ACTIVE, INACTIVE, PENDING
}

@Entity
@Table(name = "integration_test_department")
class IntegrationTestDepartment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column
    var code: String? = null
)

@Entity
@Table(name = "integration_test_user")
class IntegrationTestUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column
    var email: String? = null,

    @Column
    var age: Int? = null,

    @Column
    var active: Boolean = true,

    @Column(precision = 10, scale = 2)
    var salary: BigDecimal? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var status: IntegrationTestStatus? = null,

    @Column
    var createdAt: LocalDateTime? = null,

    @Column
    var birthDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    var department: IntegrationTestDepartment? = null
)
