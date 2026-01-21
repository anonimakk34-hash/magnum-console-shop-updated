public class GraduateStudent extends Student{
    private String researchTopic;
    public GraduateStudent(String Name, double Gpa, int id, String researchTopic){
        super(Name, Gpa, id);
        this.researchTopic = researchTopic;
    }

    @Override
    public void DisplayInfo(){
        super.DisplayInfo();
        System.out.println("Research Topic: " + researchTopic);
    }
}
