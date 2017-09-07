package xmlToGroovy;

/**
 * Created by RENGA on 4/25/2017.
 */
public class RenameTool {

    static void main(String[] args){
        File folder = new File("D:\\1_B2BEDI_Revamp\\BL\\OUT_310\\FRIESLANDGTN\\ExpectedComplete")

        for(File file : folder.listFiles()){
            file.renameTo(new File("D:\\1_B2BEDI_Revamp\\BL\\OUT_310\\FRIESLANDGTN\\" + file.name.substring(0,22) + ".in"));
        }

    }
}
