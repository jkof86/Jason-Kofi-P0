package com.revature;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import com.revature.model.Employee;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Employee emp = new Employee();
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(emp));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File f = new File("test.txt");
        if (f.exists()){System.out.println("File already exists");}

        else{

        try {
            f.createNewFile();
            f.exists();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            FileWriter fw = new FileWriter(f);
            fw.write("Writing to the second file");
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    }

}
