public class Student {
    private String name;
    private double gpa;
    private int id;
    public Student(String Name, double Gpa, int id){
        this.name = Name;
        this.gpa = Gpa;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setGpa(double gpa) {
        if(gpa >= 0.00 && gpa <= 4.00){
            this.gpa = gpa;
        }
        else{
            System.out.println("Invalid gpa!");
        }
    }
    public double getGpa(){
        return gpa;
    }

    public void DisplayInfo(){
        System.out.println("Student: " + name + " Gpa: " + gpa + " ID: " + id);
    }
}

