// Harshvardhan Sawal cs610 3198 prp

import java.util.*;
import java.lang.Math.*;
import java.io.*;
//import java.text.DecimalFormat;

public class hits_3198 {
    //private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static LinkedList<Integer> adjacencyList[];
    private static double[] h; //current hub_val
    private static double[] a; //current authority_val
    private static double[] h_prev; //previous hub_val
    private static double[] a_prev; //previous Authority_val
    private static double[] h_new; //new hub_val
    private static double[] a_new; //new Authority_val
    private static int iterations; //args[0] manually entered # of iterations
    private static int initial_value; //args[1] intial value used to control init_val for Authority and Hub for each node(page)
    private static double init_val; //initial values of page rank
    private static int nodeCount; // # of nodes(pages/vertices)
    private static int edgeCount; // # of edges between all nodes in the graph
    private static double errorRate; //breaking limit for convergence
    private static boolean iterations_isInt, initial_value_isInt, srcNode_isInt, destNode_isInt;

    static class Graph {
        int N;
        Graph(int N) {
            this.N = N;
            
            // define size of array as no. of vertices
            // and create new list (of neighbors) for every vertex
            adjacencyList = new LinkedList[N];
            for (int i = 0; i < N; i++) {
                adjacencyList[i] = new LinkedList<Integer>();
            }
        }
    }

    // Adds an edge to a directed graph
    static void addEdge(Graph graph, int src, int dest) {
        adjacencyList[src].addFirst(dest);
    }
    
    public static double[] compute_new(double[] destCol, double[] srcCol, int x)
    {
        for(int i = 0; i < nodeCount; i++){
            destCol[i] = 0.0;
        }
        double scale_factor = 0.0;
        for(int i = 0; i < nodeCount; i++){
            if(x==1){ //for Authority computations
                for(int j = 0; j < nodeCount; j++){
                    if(adjacencyList[j].contains(i)){ //Checking for outdegree neighbors
                        destCol[i] += srcCol[j];
                    }
                }
            }
            else if(x==2){ //for hub computations
                for(int j = 0; j < nodeCount; j++) {
                    if(adjacencyList[i].contains(j)) { //Checking for indegree neighbors
                        destCol[i] += srcCol[j];
                    }
                }
            }
            scale_factor += destCol[i]*destCol[i];
        }
        scale_factor = Math.sqrt(scale_factor);
        //System.out.println("\nScale factor : "+scale_factor);
        if(scale_factor == 0) scale_factor=1;
        for(int i = 0; i < nodeCount; i++){
            destCol[i] /= scale_factor;
        }
        return destCol;
    }
    
    public static void calculateHITS() {
        a_prev = a;
        h_prev = h;
        
        //compute Authority and Hubs
        a_new = compute_new(a_new, h_prev, 1);
        h_new = compute_new(h_new, a_new, 2);
        
        //Update current values
        a = a_new;
        h = h_new;
    }

    public static void intialize(){
        //Check if the N > 10, set iterations=0, initial_value=-1
        if(nodeCount>10){
            iterations = 0;
            initial_value = -1;
        }
        
        errorRate = 0.00001; //Default value
        
        if(iterations == -1){
            errorRate = 0.1;
        }
        else if(iterations == -2){
            errorRate = 0.01;
        }
        else if(iterations == -3){
            errorRate = 0.001;
        }
        else if(iterations == -4){
            errorRate = 0.0001;
        }
        else if(iterations == -5){
            errorRate = 0.00001;
        }
        else if(iterations == -6){
            errorRate = 0.000001;
        }

        //Initialization of the values are dependent on
        //initial_value
        if(!(initial_value >= -2 && initial_value <= 1)){
            System.out.println("Entered initial_value not supported!");
            System.out.println("Enter either of -2, -1, 0 or 1.");
            return;
        }
        init_val = 0;

        //Choosing the init_val based on the initial_value parameter
        if(initial_value==0){
            init_val = 0;
        }
        if(initial_value==1){
            init_val = 1;
        }
        if(initial_value==-1){
            init_val = 1 / (double) nodeCount;
        }
        if(initial_value==-2){
            init_val = 1 / (double) Math.sqrt(nodeCount);
        }

        //Setting the initial value of for Authority and Hubs from init_val
        for (int i = 0; i < nodeCount; i++) {
            h[i] = init_val;
            a[i] = init_val;
        }
    }
    
    public static void PrintHITS(){
        for (int i = 0; i < nodeCount; i++){
            System.out.printf("A/H[ %d]= %.7f/%.7f ",i,a[i],h[i]);
        }
    }

    public static void PrintLargeHITS(){
        for (int i = 0; i < 3; i++){
            System.out.printf("A/H[ %d]= %.7f/%.7f ",i,a[i],h[i]);
            System.out.println();
        }
        System.out.println("   ... other vertices omitted");
    }

    public static boolean converge(double[] cur, double[] prev){
        for (int i = 0; i < nodeCount; i++ ){
            if (Math.abs(prev[i] - cur[i]) >  errorRate){
                return false ;
            }
        }
        return true ;
    }
    
    public static void main(String[] args) throws FileNotFoundException
    {
        if(args.length!=3) {
            System.out.println("Usage : hits_3198 iterations initialvalue filename");
            return;
        }
        int v1 = 0;
        int v2 = 0;
        iterations_isInt = true;
        initial_value_isInt = true;
        Scanner in = new Scanner(System.in);
        try {
            iterations = Integer.parseInt(args[0]);
            initial_value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            //TODO: handle exception
            iterations_isInt = false;
            initial_value_isInt = false;

            System.out.println("Incorrect Parameters! Please check the values of the parameters!");
            return;
        }
        
        Scanner f = new Scanner(new File(args[2]));
        if(!((new File(args[2])).exists())){
            System.out.println("File not found. Make sure the entered file name is correct and the file exists!");
            return;
        }
        if(f.hasNextInt()) nodeCount = f.nextInt(); //Read from file line 1, 1st item
        else{
            System.out.println("File is either empty or does not have correct input format!");
            return;
        }
        if(f.hasNextInt()) edgeCount = f.nextInt(); //Read from file line 1, 2nd item
        else{
            System.out.println("File is either empty or does not have correct input format!");
            return;
        }

        if(nodeCount<0 || edgeCount<0){
            System.out.println("Incorrect format for the number of Vertices and Edges!");
            return;
        }
        
        Graph graph = new Graph(nodeCount);
        int count = 0;
        srcNode_isInt = false;
        destNode_isInt = false;
        for (int i = 0; i < edgeCount; i++) {
            if(f.hasNextInt()){
                v1 = f.nextInt();
                srcNode_isInt = true;
            }
            else{
                System.out.println("File is either empty or does not have correct input format!");
                return;
            }
            if(f.hasNextInt()){
                v2 = f.nextInt();
                destNode_isInt = true;
            }
            else{
                System.out.println("File is either empty or does not have correct input format!");
                return;
            }
            if(srcNode_isInt && destNode_isInt){
                if((v1>=0 && v1<nodeCount) && (v2>=0 && v2<nodeCount)){
                    count++;
                    addEdge(graph, v1, v2);
                }
                else{
                    System.out.println("Incorrect vertex values. Please check the vertex values for different edges!");
                    return;
                }
            }
            else{
                System.out.println("Non integer values found for different vertices. Please rectify the same and try again!");
                return;
            }
        }
        if(count<edgeCount){
            System.out.println("Mismatch between edgeCount value and actual edge pairs entered(less pairs)! Rectify the file and try again!");
            return;
        }
        if(f.hasNextInt()){
            System.out.println("Mismatch between edgeCount value and actual edge pairs entered(more pairs)! Rectify the file and try again!");
            return;
        }
        h = new double[nodeCount];
        a = new double[nodeCount];
        h_new = new double[nodeCount];
        a_new = new double[nodeCount];
        //outDegree = new double[nodeCount];
        //calculateoutdegree();

        intialize();
        
        if(iterations<1){
            //check for convergence (print till converge)
            int i = 0;
            if(nodeCount>10){
                do{
                    calculateHITS();
                    i++;
                }while(converge(a,a_prev) != true || converge(h,h_prev) != true);
                System.out.println("Iter : "+i);
                PrintLargeHITS();
            }
            else{
                do{   
                    if(i == 0){
                        System.out.print("Base : "+i+" ");
                    }
                    else{
                        System.out.print("Iter : "+i+" ");
                    }
                    //System.out.println();
                    PrintHITS();
                    calculateHITS();
                    System.out.println();
                    i++;
                }while(converge(a,a_prev) != true || converge(h,h_prev) != true);
                System.out.print("Iter : "+i+" ");
                PrintHITS();
            }
        }
        else{
            for(int i = 0; i <= iterations; i++){
                if(i==0){
                    System.out.print("Base : "+ i +" ");
                }
                else{
                    System.out.print("Iter : "+ i +" ");
                }
                //System.out.println();
                PrintHITS();
                calculateHITS();
                System.out.println();
            }
        }
    }
}