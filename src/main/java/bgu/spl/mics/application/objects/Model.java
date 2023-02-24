package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status {
        PreTrained, Training, Trained, Tested
    }

    public enum Result {
        None, Good, Bad
    }

    String name;
    Data data;
    //Student student;
    Status status;
    Result result;
    int size;


    public Model(String name, Data data, int size){
        this.status=Status.PreTrained;
        this.result=Result.None;
        this.name=name;
        this.data=data;
        this.size=size;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public int getSize() {
        return size;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
