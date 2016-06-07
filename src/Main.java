import java.util.Objects;

/**
 * Created by Rens on 2-6-2016.
 */

class Main {
    String com = null;
    String filelocation = null;
    String filename = null;

    static String elongation; //TODO: VBFixedString
    String elongationPrint;
    String force = null;
    Double time = null;
    Double pwmsignal = null;
    Float velocity = null;

    static Boolean stopNow = false;

    // TODO: file

    public Main(){
        // TODO: at start do:
    }

    public static void start() {

        //if(filelocation == ""){
        // select file location }


        if (GUI.selectedForce.equals("100Kg")){
            //TODO: Write("E");
        }
        else{
            //TODO: Write("F");
        }

        if (GUI.selectedSpeed.equals("10 mm/min")){
            //TODO: Write("1");
        }
        else if (GUI.selectedSpeed.equals("50 mm/min")){
            //TODO: Write("2");
        }
        else if (GUI.selectedSpeed.equals("100 mm/min")){
            //TODO: Write("3");
        }

        //TODO: Write("I"); to start

        //TODO: procedure loop

        // Check for cancel
        if(stopNow || Objects.equals(elongation, "a")){
            //Write("I");
            // Stop timer
        }
        else{
            //Write File
        }
    }
}
