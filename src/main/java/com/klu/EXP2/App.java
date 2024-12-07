package com.klu.EXP2;

import java.util.List;
import java.util.Scanner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

public class App {
    public static void main(String[] args) {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("Hibernate.cfg.xml").build();
        Metadata md = new MetadataSources(ssr).getMetadataBuilder().build();
        SessionFactory sf = md.getSessionFactoryBuilder().build();
        Session s = sf.openSession();
        Transaction t;
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("1. Insert Student");
            System.out.println("2. Fetch Student by ID");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. HQL: Display all student records with all columns");
            System.out.println("6. HQL: Display all student records with specific columns");
            System.out.println("7. HQL: Display names of students with GPA greater than 7");
            System.out.println("8. HQL: Delete a student by ID using parameter");
            System.out.println("9. HQL: Update student details by ID using parameter");
            System.out.println("10. HQL: Aggregate functions on GPA column");
            System.out.println("11. HCQL: Display specific columns from student records");
            System.out.println("12. HCQL: Get 5th to 10th records");
            System.out.println("13. HCQL: Apply various comparisons on GPA column");
            System.out.println("14. HCQL: Get records ordered by Student Name");
            System.out.println("15. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    while (true) {
                        Student student = new Student();
                        System.out.print("Enter Name: ");
                        student.setName(sc.next());
                        System.out.print("Enter Age: ");
                        student.setAge(sc.nextInt());
                        System.out.print("Enter Department: ");
                        student.setDepartment(sc.next());
                        System.out.print("Enter GPA: ");
                        student.setGpa(sc.nextDouble());

                        t = s.beginTransaction();
                        s.save(student);
                        t.commit();
                        System.out.println("Inserted Data");

                        System.out.print("Do you want to insert another student? (yes/no): ");
                        String insertMore = sc.next();
                        if (!insertMore.equalsIgnoreCase("yes")) {
                            break;
                        }
                    }
                    break;

                case 2:
                    System.out.print("Enter Student ID: ");
                    int id = sc.nextInt();
                    Student fetchedStudent = s.get(Student.class, id);
                    if (fetchedStudent != null) {
                        System.out.println(fetchedStudent);
                    } else {
                        System.out.println("Student not found");
                    }
                    break;

                case 3:
                    System.out.print("Enter Student ID to update: ");
                    int updateId = sc.nextInt();
                    Student updateStudent = s.get(Student.class, updateId);
                    if (updateStudent != null) {
                        System.out.print("Enter new Name: ");
                        updateStudent.setName(sc.next());
                        System.out.print("Enter new Age: ");
                        updateStudent.setAge(sc.nextInt());
                        System.out.print("Enter new Department: ");
                        updateStudent.setDepartment(sc.next());
                        System.out.print("Enter new GPA: ");
                        updateStudent.setGpa(sc.nextDouble());

                        t = s.beginTransaction();
                        s.update(updateStudent);
                        t.commit();
                        System.out.println("Updated Data");
                    } else {
                        System.out.println("Student not found");
                    }
                    break;

                case 4:
                    System.out.print("Enter Student ID to delete: ");
                    int deleteId = sc.nextInt();
                    Student deleteStudent = s.get(Student.class, deleteId);
                    if (deleteStudent != null) {
                        t = s.beginTransaction();
                        s.delete(deleteStudent);
                        t.commit();
                        System.out.println("Deleted Data");
                    } else {
                        System.out.println("Student not found");
                    }
                    break;

                case 5:
                    List<Student> studentsAllColumns = s.createQuery("from Student", Student.class).list();
                    for (Student student : studentsAllColumns) {
                        System.out.println(student);
                    }
                    break;

                case 6:
                    List<Object[]> studentsSpecificColumns = s.createQuery("select id, name, gpa from Student", Object[].class).list();
                    for (Object[] student : studentsSpecificColumns) {
                        System.out.println("ID: " + student[0] + ", Name: " + student[1] + ", GPA: " + student[2]);
                    }
                    break;

                case 7:
                    List<String> studentNames = s.createQuery("select name from Student where gpa > 7", String.class).list();
                    for (String name : studentNames) {
                        System.out.println("Name: " + name);
                    }
                    break;

                case 8:
                    System.out.print("Enter Student ID to delete using parameter: ");
                    int deleteIdParam = sc.nextInt();
                    t = s.beginTransaction();
                    Query deleteQueryParam = s.createQuery("delete from Student where id = :id");
                    deleteQueryParam.setParameter("id", deleteIdParam);
                    int resultDeleteParam = deleteQueryParam.executeUpdate();
                    t.commit();
                    System.out.println("Number of records deleted: " + resultDeleteParam);
                    break;

                case 9:
                    System.out.print("Enter Student ID to update using parameter: ");
                    int updateIdParam = sc.nextInt();
                    t = s.beginTransaction();
                    Query updateQueryParam = s.createQuery("update Student set name = :name, gpa = :gpa where id = :id");
                    System.out.print("Enter new Name: ");
                    updateQueryParam.setParameter("name", sc.next());
                    System.out.print("Enter new GPA: ");
                    updateQueryParam.setParameter("gpa", sc.nextDouble());
                    updateQueryParam.setParameter("id", updateIdParam);
                    int resultUpdateParam = updateQueryParam.executeUpdate();
                    t.commit();
                    System.out.println("Number of records updated: " + resultUpdateParam);
                    break;

                case 10:
                    Query<Object[]> aggregateQuery = s.createQuery("select count(*), sum(gpa), avg(gpa), min(gpa), max(gpa) from Student", Object[].class);
                    Object[] aggregateResults = aggregateQuery.uniqueResult();
                    System.out.println("Count: " + aggregateResults[0] + ", Sum: " + aggregateResults[1] + ", Avg: " + aggregateResults[2] + ", Min: " + aggregateResults[3] + ", Max: " + aggregateResults[4]);
                    break;

                case 11:
                    Criteria criteriaSpecificColumns = s.createCriteria(Student.class)
                            .setProjection(Projections.projectionList()
                                    .add(Projections.property("id"))
                                    .add(Projections.property("name"))
                                    .add(Projections.property("gpa")));
                    List<Object[]> specificColumnsList = criteriaSpecificColumns.list();
                    for (Object[] row : specificColumnsList) {
                        System.out.println("ID: " + row[0] + ", Name: " + row[1] + ", GPA: " + row[2]);
                    }
                    break;

                case 12:
                    Criteria criteriaFifthToTenth = s.createCriteria(Student.class)
                            .setFirstResult(4)
                            .setMaxResults(6);
                    List<Student> fifthToTenthRecords = criteriaFifthToTenth.list();
                    for (Student student : fifthToTenthRecords) {
                        System.out.println(student);
                    }
                    break;

                case 13:
                    CriteriaBuilder cb = s.getCriteriaBuilder();
                    CriteriaQuery<Student> cq = cb.createQuery(Student.class);
                    Root<Student> root = cq.from(Student.class);
                    Predicate predicate = cb.greaterThan(root.get("gpa"), 7.0);
                    cq.where(predicate);
                    List<Student> comparisonResults = s.createQuery(cq).getResultList();
                    for (Student student : comparisonResults) {
                        System.out.println(student);
                    }
                    break;

                case 14:
                    Criteria criteriaOrder = s.createCriteria(Student.class).addOrder(Order.asc("name"));
                    List<Student> ascendingOrderList = criteriaOrder.list();
                    for (Student student : ascendingOrderList) {
                        System.out.println(student);
                    }
                    break;

                case 15:
                    System.out.println("Exiting...");
                    sc.close();
                    s.close();
                    sf.close();
                    return;

                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }
}
