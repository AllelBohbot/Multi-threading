//package bgu.spl.mics.application;
////
////import bgu.spl.mics.MicroService;
////import bgu.spl.mics.PublishConferenceCallback;
//import bgu.spl.mics.application.objects.CPU;
////import bgu.spl.mics.application.objects.Model;
////import bgu.spl.mics.application.objects.Student;
////import bgu.spl.mics.application.services.StudentService;
////import com.google.gson.Gson;
////import javafx.util.Pair;
////
////import java.io.Reader;
////import java.nio.file.Files;
////import java.nio.file.Paths;
////import java.util.ArrayList;
//
//import bgu.spl.mics.application.objects.Student;
//import com.google.gson.Gson;
//import bgu.spl.mics.Parser;
//import javafx.util.Pair;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.io.Reader;
//
//
//
//
//
///** This is the Main class of Compute Resources Management System application. You should parse the input file,
// * create the different instances of the objects, and run the system.
// * In the end, you should output a text file.
// */
package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Parser;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.Reader;
import java.sql.Time;
import java.util.ArrayList;

import static bgu.spl.mics.application.objects.GPU.Type.*;


public class CRMSRunner {

    public static void createOutputFile(ArrayList<Student> studentArray, ArrayList<ConfrenceInformation> conferenceArray) {
        Statistics statistics = new Statistics();
        try {
            FileWriter file = new FileWriter("output.txt");
            file.write("Total CPUs time used: " + statistics.getCpuUse() + "\n");
            file.write("Total GPUs time used: " + statistics.getGpuUse() + "\n");
            file.write("Amount of batches processed by the CPUs: " + statistics.getTotalNumOfprocessedDB() + "\n");
            for (Student student:studentArray) {
                file.write("Student name: " + student.getName() + "\n");
                file.write("Trained Model:\n");
                for (Model model : student.getModels()) {
                    if (model.getStatus() == Model.Status.Trained || model.getStatus() == Model.Status.Tested)
                        file.write(model.getName() + "\n");
                }
                file.write("Published Model:\n");
                for (Model model : student.getPublishedModel()) {
                    file.write(model.getName() + "\n");
                }
                file.write("Paper read: " + student.getPapersRead() + "\n");
            }
                for (ConfrenceInformation confrenceInformation:conferenceArray){
                    file.write("Conference name: " + confrenceInformation.getName()+"\n");
                    file.write("Publications:\n");
                    for (String modelName:confrenceInformation.getPublications()){
                        file.write(modelName+"\n");
                    }
                }
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputFilePath = args[0].toString();
        ArrayList<Parser.Students> students = new ArrayList<>();
        ArrayList<Student> studentArrayList = new ArrayList<>();
        ArrayList<ConfrenceInformation> conferenceArrayList = new ArrayList<>();
        ArrayList<Model> modelArrayList;
        ArrayList<Thread> threadsArrayList = new ArrayList<>();
        ArrayList<MicroService> microServiceArrayList = new ArrayList<>();
        try {
            // create Gson instance
            Gson gson = new Gson();
            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(inputFilePath));
            // convert JSON file to map
            Parser input = gson.fromJson(reader, Parser.class);
            students = input.getStudents();
            for (int i = 0; i < students.size(); i++) {
                modelArrayList = new ArrayList<>();
                for (int j = 0; j < students.get(i).getModels().size(); j++) {
                    Data temp=new Data(students.get(i).getModels().get(j).getType(), students.get(i).getModels().get(j).getSize());
                    modelArrayList.add(new Model(students.get(i).getModels().get(j).getName(), temp, students.get(i).getModels().get(j).getSize()));
                }
                StudentService studentService = new StudentService(students.get(i).getName(), students.get(i).getDepartment(), students.get(i).getStatus(), 0, 0, modelArrayList);
                studentArrayList.add(studentService.getStudent());
                microServiceArrayList.add(studentService);
                threadsArrayList.add(new Thread(studentService));
            }

            for (Integer i = 0; i < input.getGPUS().size(); i++) {
                GPU.Type type;
                if (input.getGPUS().get(i) == "RTX3090") {
                    type = RTX3090;
                } else if (input.getGPUS().get(i) == "RTX2080") {
                    type = RTX2080;
                } else
                    type = GTX1080;
                GPUService gpuService = new GPUService(i.toString(), type);
                microServiceArrayList.add(gpuService);
                threadsArrayList.add(new Thread(gpuService));

            }

            for (Integer i = 0; i < input.getCPUS().size(); i++) {
                CPUService cpuService = new CPUService(i.toString(), input.getCPUS().get(i));
                microServiceArrayList.add(cpuService);
                threadsArrayList.add(new Thread(cpuService));
            }

            for (Integer i = 0; i < input.getConferences().size(); i++) {
                ConferenceService conferenceService = new ConferenceService(input.getConferences().get(i).getName(), input.getConferences().get(i).getDate());
                conferenceArrayList.add(conferenceService.getConfrenceInformation());
                microServiceArrayList.add(conferenceService);
                threadsArrayList.add(new Thread(conferenceService));
            }
            //threadsArrayList.add(new Thread(new TimeService(input.getDuration(), input.getTickTime())));
            Thread timeThread = new Thread(new TimeService(input.getDuration(), input.getTickTime()));


            for (int i = 0; i < threadsArrayList.size(); i++) {
                threadsArrayList.get(i).start();
            }
            timeThread.start();
            threadsArrayList.get(1).join();
            createOutputFile(studentArrayList, conferenceArrayList);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            }
    }
}
