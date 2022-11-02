import java.io.*;
import java.util.*;

public class VMTranslator {

    private static int labelCount = 0;

    public static String seg(String segment) {
        Map<String, String> segs = new HashMap<>();
        segs.put("static", "16");
        segs.put("local", "LCL");
        segs.put("argument", "ARG");
        segs.put("this", "THIS");
        segs.put("that", "THAT");
        segs.put("temp", "5");

        return segs.get(segment);
    }

    public static String label() {
        String label = "";
        int count = labelCount;
        while (count > 25) {
            label += (char) (26 + (int) 'A');
            count -= 26;
        }
        label += (char) (count + (int) 'A');
        labelCount ++;
        return label;
    }

    public static String terminate() {
        return "(END)\n@END\n0;JMP\n";
    }
    /** Generate Hack Assembly code for a VM push operation assessed in Practical Assignment 6 */
    public static String vm_push(String segment, int offset) {
        if (segment.equalsIgnoreCase("constant")) {
            return "@" + offset + "\n" +
                    "D=A\n" +
                    "@SP\n" +
                    "AM=M+1\n" +
                    "A=A-1\n" +
                    "M=D\n";
        }
        if (segment.equalsIgnoreCase("pointer")) {
            return "@" + (offset == 0 ? "THIS" : "THAT") + "\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "AM=M+1\n" +
                    "A=A-1\n" +
                    "M=D\n";
        }
        if (segment.equalsIgnoreCase("static") || segment.equalsIgnoreCase("temp")) {
            return "@" + offset + "\n" +
                    "D=A\n" +
                    "@" + seg(segment) + "\n" +
                    "A=D+A\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "AM=M+1\n" +
                    "A=A-1\n" +
                    "M=D\n";
        }
        return "@" + offset + "\n" +
                "D=A\n" +
                "@" + seg(segment) + "\n" +
                "A=D+M\n" +
                "D=M\n" +
                "@SP\n" +
                "AM=M+1\n" +
                "A=A-1\n" +
                "M=D\n";
    }

    /** Generate Hack Assembly code for a VM pop operation assessed in Practical Assignment 6 */
    public static String vm_pop(String segment, int offset){

        if (segment.equalsIgnoreCase("pointer")) {
            return "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n"+
                    "@" + (offset == 0 ? "THIS" : "THAT") + "\n" +
                    "M=D\n";
        }

        if (segment.equalsIgnoreCase("static") || segment.equalsIgnoreCase("temp")) {
            return "@" + offset + "\n" +
                    "D=A\n" +
                    "@" + seg(segment) + "\n" +
                    "D=D+A\n" +
                    "@13\n" +
                    "M=D\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "@13\n" +
                    "A=M\n" +
                    "M=D\n";
        }

        return "@" + offset + "\n" +
                "D=A\n" +
                "@" + seg(segment) + "\n" +
                "D=D+M\n" +
                "@13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@13\n" +
                "A=M\n" +
                "M=D\n";
    }

    /** Generate Hack Assembly code for a VM add operation assessed in Practical Assignment 6 */
    public static String vm_add(){
        return "@SP\nAM=M-1\nD=M\nA=A-1\nM=M+D\n";
    }

    /** Generate Hack Assembly code for a VM sub operation assessed in Practical Assignment 6 */
    public static String vm_sub(){
        return "@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\n";
    }

    /** Generate Hack Assembly code for a VM neg operation assessed in Practical Assignment 6 */
    public static String vm_neg(){
        return "@SP\nA=M-1\nM=-M\n";
    }

    /** Generate Hack Assembly code for a VM eq operation assessed in Practical Assignment 6 */
    public static String vm_eq(){
        String a = label();
        String b = label();
        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=D-M\n" +
                "@" + a + "\n" +
                "D;JEQ\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@" + b + "\n" +
                "0;JMP\n" +
                "(" + a + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(" + b + ")\n";
    }

    /** Generate Hack Assembly code for a VM gt operation assessed in Practical Assignment 6 */
    public static String vm_gt(){
        String a = label();
        String b = label();
        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=D-M\n" +
                "@" + a + "\n" +
                "D;JLT\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@" + b + "\n" +
                "0;JMP\n" +
                "(" + a + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(" + b + ")\n";
    }

    /** Generate Hack Assembly code for a VM lt operation assessed in Practical Assignment 6 */
    public static String vm_lt(){
        String a = label();
        String b = label();
        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=D-M\n" +
                "@" + a + "\n" +
                "D;JGT\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@" + b + "\n" +
                "0;JMP\n" +
                "(" + a + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(" + b + ")\n";
    }

    /** Generate Hack Assembly code for a VM and operation assessed in Practical Assignment 6 */
    public static String vm_and(){
        return "@SP\nAM=M-1\nD=M\nA=A-1\nM=M&D\n";
    }

    /** Generate Hack Assembly code for a VM or operation assessed in Practical Assignment 6 */
    public static String vm_or(){
        return "@SP\nAM=M-1\nD=M\nA=A-1\nM=M|D\n";
    }

    /** Generate Hack Assembly code for a VM not operation assessed in Practical Assignment 6 */
    public static String vm_not(){
        return "@SP\nA=M-1\nM=!M\n";
    }

    /** Generate Hack Assembly code for a VM label operation assessed in Practical Assignment 7 */
    public static String vm_label(String label){
        return "";
    }

    /** Generate Hack Assembly code for a VM goto operation assessed in Practical Assignment 7 */
    public static String vm_goto(String label){
        return "";
    }

    /** Generate Hack Assembly code for a VM if-goto operation assessed in Practical Assignment 7 */
    public static String vm_if(String label){
        return "";
    }

    /** Generate Hack Assembly code for a VM function operation assessed in Practical Assignment 7 */
    public static String vm_function(String function_name, int n_vars){
        return "";
    }

    /** Generate Hack Assembly code for a VM call operation assessed in Practical Assignment 7 */
    public static String vm_call(String function_name, int n_args){
        return "";
    }

    /** Generate Hack Assembly code for a VM return operation assessed in Practical Assignment 7 */
    public static String vm_return(){
        return "";
    }

    /** A quick-and-dirty parser when run standalone. */ 
    public static void main(String[] args){
        if(args.length > 0){
            try {
//                String name = "tests" + File.separator + args[0];
//                Scanner sc = new Scanner(new File(name + ".vm"));
//                Writer w = new FileWriter(name + ".asm");
                Scanner sc = new Scanner(new File(args[0]));
                Writer w = new FileWriter(args[0].replace(".vm", ".asm"));
                while (sc.hasNextLine()) {
                    String[] tokens = sc.nextLine().trim().toLowerCase().split("\\s+");
                    if(tokens.length==1){
                        if(tokens[0].equals("add")){
                            w.write(vm_add());
                        } else if(tokens[0].equals("sub")){
                            w.write(vm_sub());
                        } else if(tokens[0].equals("neg")){
                            w.write(vm_neg());
                        } else if(tokens[0].equals("eq")){
                            w.write(vm_eq());
                        } else if(tokens[0].equals("gt")){
                            w.write(vm_gt());
                        } else if(tokens[0].equals("lt")){
                            w.write(vm_lt());
                        } else if(tokens[0].equals("and")){
                            w.write(vm_and());
                        } else if(tokens[0].equals("or")){
                            w.write(vm_or());
                        } else if(tokens[0].equals("not")){
                            w.write(vm_not());
                        } else if(tokens[0].equals("return")){
                            w.write(vm_return());
                        }
                    } else if(tokens.length==2){
                        if(tokens[0].equals("label")){
                            w.write(vm_label(tokens[1]));
                        } else if(tokens[0].equals("goto")){
                            w.write(vm_goto(tokens[1]));
                        } else if(tokens[0].equals("if")){
                            w.write(vm_if(tokens[1]));
                        }
                    } else if(tokens.length==3){
                        int t2;
                        try {
                            t2 = Integer.parseInt(tokens[2]);
                        } catch (Exception e) {
                            System.err.println("Unable to parse int.");
                            break;
                        }
                        if(tokens[0].equals("push")){
                            w.write(vm_push(tokens[1],t2));
                        } else if(tokens[0].equals("pop")){
                            w.write(vm_pop(tokens[1],t2));
                        } else if(tokens[0].equals("function")){
                            w.write(vm_function(tokens[1],t2));
                        } else if(tokens[0].equals("call")){
                            w.write(vm_call(tokens[1],t2));
                        }
                    }
                }
                w.write(terminate());
                sc.close();
                w.flush();
                w.close();
            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
        
}