/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.civsavefilelib;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import nl.hyranasoftware.civsavefilelib.civ6.Civ6Analyzer;

/**
 *
 * @author danny_000
 */
//https://stackoverflow.com/questions/28206027/modifying-contents-of-a-binary-file-in-java
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String fileName = "C:\\Users\\danny_000\\programming\\civsavefilelib\\src\\main\\java\\nl\\hyranasoftware\\civsavefilelib\\save1.Civ6Save";

        File file = new File(fileName);
        RandomAccessFile fh = new RandomAccessFile(file, "rw");
        Civ6Analyzer civ6 = new Civ6Analyzer();
        civ6.analyseSaveFile(fh);
       

    }

}
