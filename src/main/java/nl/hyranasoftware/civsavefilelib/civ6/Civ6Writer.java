/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.civsavefilelib.civ6;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author danny_000
 */
public class Civ6Writer {

    final int[] aiOrPlayer = new int[]{0x95, 0xb9, 0x42, 0xce};
    final int[] civBegin = new int[]{0x31, 0xEB, 0x88, 0x62};
    final int[] playerNameArray = new int[]{0xfd, 0x6b, 0xb9, 0xda};
    final int[] civEnd = new int[]{0x58, 0xba};
    final int[] civName = new int[]{0x2f, 0x5c, 0x5e, 0x9d};
    final int[] civLevel = new int[]{0xAB, 0x55, 0xCA, 0x05};
    final int[] delimiter = new int[]{0x21, 0x01};
    final int[] playerPassword = new int[]{0x6c, 0xd1, 0x7c, 0x6e};
    final int[] beforePlayerName = new int[]{0xc7, 0xfc,0xe4, 0xdf};
    
    
    final byte[] emptyByte = new byte[]{0x00};
    private boolean insert(RandomAccessFile fh, long offset, byte[] content) {
        try {
            RandomAccessFile r = fh;
            //Temporary file where the data gets transferred to
            RandomAccessFile rtemp = new RandomAccessFile(new File("temp" + "~"), "rw");
            //FileSize
            long fileSize = r.length();
            //Initiating channels
            FileChannel sourceChannel = r.getChannel();
            FileChannel targetChannel = rtemp.getChannel();

            //Transfer everything below to temp file
            sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
            //Truncate the file
            sourceChannel.truncate(offset);
            //Go to offset
            r.seek(offset);
            //Write the changes
            r.write(content);
            //Find the new Offset
            long newOffset = r.getFilePointer();
            //Reset temp file to begin of file
            targetChannel.position(0L);
            //Transfer from temp file to existing
            sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));
            //We're done here
            targetChannel.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Civ6Writer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Civ6Writer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public void changeCivToHuman(Location loc, RandomAccessFile fh, String name) throws IOException {
        int namecount = name.getBytes().length + 1;
        byte nameCountByte = (byte) namecount;
        byte[] playerNameField = new byte[]{(byte)0xfd, 0x6b, (byte)0xb9, (byte)0xda, 0x05, 0x00, 0x00, 0x00, nameCountByte, 0x00, 0x00, 0x21, 0x01, 0x00, 0x00, 0x00};
        changeHumanity(fh, loc, true);
        fh.seek(loc.getBegin());

        byte[] nameArray = (byte[])ArrayUtils.addAll(name.getBytes(StandardCharsets.US_ASCII), emptyByte);
        byte[] insertBytesArray = (byte[])ArrayUtils.addAll(playerNameField, nameArray);

        int c;
        while(true){
            c = fh.read();
            if(c == beforePlayerName[0]){
                c = fh.read();
                if(c == beforePlayerName[1]){
                    c = fh.read();
                    if(c == beforePlayerName[2]){
                        insert(fh, (fh.getFilePointer() - 3), insertBytesArray);
                        break;
                    }
                }
            }
        }
    }
    
    private boolean changeHumanity(RandomAccessFile fh, Location loc, boolean makeHuman) throws IOException {
        fh.seek(loc.getBegin());
        int c = 0;
        boolean human = false;
        boolean foundHumanity = false;
        while (!foundHumanity) {
            c = fh.read();
            if (c == aiOrPlayer[0]) {
                c = fh.read();
                if (c == aiOrPlayer[1]) {
                    c = fh.read();
                    if (c == aiOrPlayer[2]) {
                        while (!foundHumanity) {
                            c = fh.read();
                            if (c != 0x00) {
                                fh.seek(fh.getFilePointer() -1);
                                if(makeHuman){
                                    fh.writeByte(0x03);
                                    
                                    foundHumanity = true;
                                }else{
                                    fh.writeByte(0x01);
                                     foundHumanity = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(human);
        return human;

    }

}
