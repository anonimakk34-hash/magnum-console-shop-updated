import java.util.ArrayList;
import java.util.Collections;

// The Interface
interface Payable {
    double getPaymentAmount();
}


// The Parent Class (Person)
class Person implements Payable, Comparable<Person> {
    private static int id_gen = 1;

    private int id;
    private String name;
    private String surname;

    public Person() {
        this.id = id_gen++;
    }

    public Person(String name, String surname) {
        this();
        this.name = name;
        this.surname = surname;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }


    public String getPosition() {
        return "Person";
    }

    @Override
    public String toString() {
        return id + ". " + name + " " + surname;
    }

    @Override
    public double getPaymentAmount() {
        return 0.00;
    }

    @Override
    public int compareTo(Person other) {
        return Double.compare(this.getPaymentAmount(), other.getPaymentAmount());
    }
}

//The Subclasses (Student & Employee)
class Student extends Person {
    private double gpa;

    public Student() {
        super();
    }

    public Student(String name, String surname, double gpa) {
        super(name, surname);
        this.gpa = gpa;
    }

    public Double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    @Override
    public String toString() {
        return "Student: " + super.toString();
    }

    @Override
    public double getPaymentAmount() {
        // Changed to 36660.00 to match your assignment text
        if (gpa > 2.67) {
            return 36660.00;
        } else {
            return 0.00;
        }
    }
}

class Employee extends Person {
    private String position;
    private double salary;

    public Employee() {
        super();
    }

    public Employee(String name, String surname, String position, double salary) {
        super(name, surname);
        this.position = position;
        this.salary = salary;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public String toString() {
        return "Employee: " + super.toString();
    }

    @Override
    public double getPaymentAmount() {
        return salary;
    }
}

// The Main Class
public class Main {
    public static void main(String[] args) {
        ArrayList<Person> people = new ArrayList<>();

        people.add(new Employee("John", "Lennon", "Musician", 27045.78));
        people.add(new Employee("George", "Harrison", "Musician", 50000.00));
        people.add(new Student("Ringo", "Starr", 2.0));
        people.add(new Student("Paul", "McCartney", 3.5));

        Collections.sort(people);


        printData(people);
    }

    public static void printData(Iterable<Person> people) {
        for (Person p : people) {
            System.out.println(p.toString() + " earns " + String.format("%.2f", p.getPaymentAmount()) + " tenge");
        }
    }
}