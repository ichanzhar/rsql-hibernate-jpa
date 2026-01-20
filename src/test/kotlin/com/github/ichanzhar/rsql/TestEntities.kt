package com.github.ichanzhar.rsql

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

enum class Status {
    ACTIVE, INACTIVE, PENDING
}

@Entity
@Table(name = "test_user")
class TestUser(
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

    @Column
    var salary: BigDecimal? = null,

    @Column
    var rating: Double? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var status: Status? = null,

    @Column
    var createdAt: LocalDateTime? = null,

    @Column
    var birthDate: LocalDate? = null,

    @Column
    var uuid: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    var department: TestDepartment? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var tags: MutableList<TestTag> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    var roles: MutableSet<String> = mutableSetOf()
)

@Entity
@Table(name = "test_department")
class TestDepartment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column
    var code: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: TestDepartment? = null,

    @OneToMany(mappedBy = "department", cascade = [CascadeType.ALL])
    var users: MutableList<TestUser> = mutableListOf()
)

@Entity
@Table(name = "test_tag")
class TestTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: TestUser? = null
)
