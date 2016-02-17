
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.text.DecimalFormat;


public class Main
{
    private static final int INPUT_NEURONS = 9;
    private static final int HIDDEN_NEURONS = 100;
    private static final int OUTPUT_NEURONS = 6;

    private static final double LEARN_RATE = 0.45;    // Rho.
    private static final double NOISE_FACTOR = 0;
    private static final int TRAINING_REPS = 8000000;

    /// 외이트 값들
    // Input to Hidden Weights (with Biases).
    private static double wih[][] = new double[INPUT_NEURONS + 1][HIDDEN_NEURONS];
    // Hidden to Output Weights (with Biases).
    private static double who[][] = new double[HIDDEN_NEURONS + 1][OUTPUT_NEURONS];

    // Unit errors.
    private static double erro[] = new double[OUTPUT_NEURONS];
    private static double errh[] = new double[HIDDEN_NEURONS];

    // Activations.
    private static double inputs[] = new double[INPUT_NEURONS];
    private static double hidden[] = new double[HIDDEN_NEURONS];
    private static double target[] = new double[OUTPUT_NEURONS];
    private static double actual[] = new double[OUTPUT_NEURONS];
    
    //인풋수와 아웃풋 수.
    private static final int MAX_SAMPLES = 13;

    //인풋파일
    private static float trainInputs[][] = new float[MAX_SAMPLES][];
                                                      // 아웃풋 파일.
                                                      
                                                      //인풋파일
  private static float testingInputs[][] = new float[][] {{14,27,30,31,40,42,4,3,4}};
                                                    
    private static float trainOutput[][] = new float[MAX_SAMPLES][];

                                         // 지난주 1등번호 2등번호  == 다음주 1등 번호
                           
    private static void LoadTrainingSet()
    {
    	BufferedReader textReader = null;
		try
		{
			textReader = new BufferedReader(new FileReader("transedOutput.txt"));
		}
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String readedString = ""; // 읽어온 문장을 저정하는 변수입니다. 
		
    	for(int i = 0 ; i < MAX_SAMPLES;i++)
    	{
    		try
			{
				readedString = textReader.readLine();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		String[] splitedArray = readedString.split(","); // 읽어온 한줄을 " " 공백으로 분리해 줍니다. 분리하면 문장이 단어들로 변합니다.
    		int j = 0;
    		trainInputs[i] = new float[INPUT_NEURONS];
    		for(j = 0 ; j < INPUT_NEURONS;j++)
        	{
    			trainInputs[i][j] = (float) (0.01* (Integer.parseInt( splitedArray[j])));
        	}
    		trainOutput[i] = new float[OUTPUT_NEURONS];
	    	for(int k = 0 ; k < OUTPUT_NEURONS;k++ ,j++)
	    	{
	    		trainOutput[i][k] = (float) (0.01 * Integer.parseInt( splitedArray[j]));
	    	}
    	}
    	try
		{
			textReader.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
                                         
    private static void NeuralNetwork()
    {
        int sample = 0;

        assignRandomWeights();

        // Train the network.
        for(int epoch = 0; epoch < TRAINING_REPS; epoch++)
        {
        	if(epoch%10000 == 0)
        		System.out.println("ho! im running" + epoch);
            sample += 1;
            if(sample == MAX_SAMPLES){
                sample = 0;
            }

            for(int i = 0; i < INPUT_NEURONS; i++)
            {
                inputs[i] = trainInputs[sample][i];
            } // i

            for(int i = 0; i < OUTPUT_NEURONS; i++)
            {
                target[i] = trainOutput[sample][i];
            } // i

            feedForward();

            backPropagate();

        } // epoch

        getTrainingStats();
        System.out.println("hitted?");
		
        System.out.println("\nTest network against original input:");
        testNetworkTraining();
		
        System.out.println("\nTest network against noisy input:");
        testNetworkWithNoise1();
		
        return;
    }
    
    private static void GetNumber()
    {
    	Scanner sc = new Scanner(System.in);
        
        for(int i = 0 ; i< INPUT_NEURONS;i++)
        {
        	if(i < 7)
        	{
        		System.out.println((i+1)+"번 째 인풋을 입력해주세요 45이하로요!");
        		float tempInputValue = (float)Integer.parseInt(sc.nextLine());
        		if(tempInputValue <1 || tempInputValue>45)
        		{
        			i--;
        			System.out.println((i+1)+"번 째 값이 너무 커요! 다시 입력해주세요");
        		}
        		else if(testingInputs[0][0] == tempInputValue
        				|| testingInputs[0][1] == tempInputValue
        						||testingInputs[0][2] == tempInputValue
        								||testingInputs[0][3] == tempInputValue
        										||testingInputs[0][4] == tempInputValue
        												||testingInputs[0][5] == tempInputValue
        				)
        		{
        			i--;
        			System.out.println((i+1)+"번 째 값이 중복되었습니다! 다시 입력해주세요");
        		}
        		else
        		{
        			testingInputs[0][i] = tempInputValue;
        		}
        	}
        	else
        	{
        		if(i == 7)
        		{
        			System.out.println("전주의 주요 컬러 패턴은요?(4 이하입니다!");
        		}
        		else if(i == 8)
        		{
        			System.out.println("전주의 보조 컬러 패턴은요?(4 이하입니다!");
        		}
        		float tempInputValue = (float)Integer.parseInt(sc.nextLine());
        		if(tempInputValue <0 || tempInputValue>4 )
        		{
        			i--;
        			System.out.println(i+"번 째 값이 너무 커요! 다시 입력해주세요");
        		}
        		else
        		{
        			testingInputs[0][i] = tempInputValue;
        		}
        	}
        	
        	
        }
    	for(int i = 0 ; i<6; i++)
    	{
        	float smollInt = testingInputs[0][i];
        	for(int j = i+1 ; j< 6;j++)
        	{
        		if(smollInt > testingInputs[0][j])
            	{
        			float tempValue = testingInputs[0][j];
        			testingInputs[0][j] = smollInt;
        			testingInputs[0][i] = tempValue;
        			smollInt = tempValue;
            	}
        	}
    	}
        
        System.out.println("\n 당첨 번호는:");
        testNetworkWithNoise1();
        return;
    }
    
    private static void AutoNumber()
    {
    	/*
    	for(int j = 0 ; j < 5;j++)
    	{
    		if(j == 0)
    		{
	    		testingInputs[0][7] = 0;
	    		testingInputs[0][8] = 0;
    		}
    		else if( j == 1)
    		{
	    		testingInputs[0][7] = 0;
	    		testingInputs[0][8] = 1;
    			
    		}
    		else if( j == 2)
    		{
	    		testingInputs[0][7] = 1;
	    		testingInputs[0][8] = 0;
    			
    		}
    		else if( j == 3)
    		{
	    		testingInputs[0][7] = 1;
	    		testingInputs[0][8] = 1;
    			
    		}
    		else if( j == 4)
    		{
	    		testingInputs[0][7] = j;
	    		testingInputs[0][8] = j;
    		}
    	}*/
        
        System.out.println("\n 당첨 번호는:");
        testNetworkWithNoise1();
        return;
    }

    private static void getTrainingStats()
    {
        double sum = 0.0;
        for(int i = 0; i < MAX_SAMPLES; i++)
        {
            for(int j = 0; j < INPUT_NEURONS; j++)
            {
                inputs[j] = trainInputs[i][j];
            } // j

            for(int j = 0; j < OUTPUT_NEURONS; j++)
            {
                target[j] = trainOutput[i][j];
            } // j

            feedForward();

            if(maximum(actual) == maximum(target)){
                sum += 1;
            }else{
                System.out.println(inputs[0] + "\t" + inputs[1] + "\t" + inputs[2] + "\t" + inputs[3]);
              System.out.println(maximum(actual) + "\t" + maximum(target));
            }
        } // i

        System.out.println("Network is " + ((double)sum / (double)MAX_SAMPLES * 100.0) + "% correct.");

        return;
    }

    private static void testNetworkTraining()
    {
        // This function simply tests the training vectors against network.
        for(int i = 0; i < MAX_SAMPLES; i++)
        {
            for(int j = 0; j < INPUT_NEURONS; j++)
            {
                inputs[j] = trainInputs[i][j];
            } // j
            
            feedForward();

            System.out.print("input is ");
            for(int j = 0; j < INPUT_NEURONS; j++)
            {
                System.out.print(inputs[j] + "\t");
            } // j

            System.out.print("\n");
            for(int k = 0 ; k < actual.length;k++)
            {
            	float tempValue = (float) actual[k];
            	if(tempValue < 0.009)
            	{
            		tempValue = 0;
            	}
            	System.out.print("number "+ (k+1) +" : " + tempValue+ "\t");
            }
            System.out.print("\n");
        } // i
        
        return;
    }

    private static void testNetworkWithNoise1()
    {
        // This function adds a random fractional value to all the training
        // inputs greater than zero.
        DecimalFormat dfm = new java.text.DecimalFormat("###0.0");
        
        for(int i = 0; i < 1; i++)
        {
            for(int j = 0; j < INPUT_NEURONS; j++)
            {
                inputs[j] = 0.01* testingInputs[i][j];
            } // j
            
            feedForward();
            
            for(int j = 0; j < INPUT_NEURONS; j++)
            {
                System.out.print(inputs[j] + "\t");
            } // j

            System.out.print("\n");
            for(int k = 0 ; k < actual.length;k++)
            {
            	float tempValue = (float) actual[k];
            	if(tempValue < 0.009)
            	{
            		tempValue = 0;
            	}
            		
            	System.out.print("Output: " + tempValue + "\n");
            }
        } // i

        return;
    }

    private static int maximum(final double[] vector)
    {
        // This function returns the index of the maximum of vector().
        int sel = 0;
        double max = vector[sel];

        for(int index = 0; index < OUTPUT_NEURONS; index++)
        {
            if(vector[index] > max){
                max = vector[index];
                sel = index;
            }
        }
        return sel;
    }

    private static void feedForward()
    {
        double sum = 0.0;

        // Calculate input to hidden layer.
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            sum = 0.0;
            for(int inp = 0; inp < INPUT_NEURONS; inp++)
            {
                sum += inputs[inp] * wih[inp][hid];
            } // inp

            sum += wih[INPUT_NEURONS][hid]; // Add in bias.
            hidden[hid] = sigmoid(sum);
        } // hid

        // Calculate the hidden to output layer.
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            sum = 0.0;
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                sum += hidden[hid] * who[hid][out];
            } // hid

            sum += who[HIDDEN_NEURONS][out]; // Add in bias.
            actual[out] = sigmoid(sum);
        } // out
        return;
    }

    private static void backPropagate()
    {
        // Calculate the output layer error (step 3 for output cell).
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            erro[out] = (target[out] - actual[out]) * sigmoidDerivative(actual[out]);
        }

        // Calculate the hidden layer error (step 3 for hidden cell).
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            errh[hid] = 0.0;
            for(int out = 0; out < OUTPUT_NEURONS; out++)
            {
                errh[hid] += erro[out] * who[hid][out];
            }
            errh[hid] *= sigmoidDerivative(hidden[hid]);
        }

        // Update the weights for the output layer (step 4).
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                who[hid][out] += (LEARN_RATE * erro[out] * hidden[hid]);
            } // hid
            who[HIDDEN_NEURONS][out] += (LEARN_RATE * erro[out]); // Update the bias.
        } // out

        // Update the weights for the hidden layer (step 4).
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            for(int inp = 0; inp < INPUT_NEURONS; inp++)
            {
                wih[inp][hid] += (LEARN_RATE * errh[hid] * inputs[inp]);
            } // inp
            wih[INPUT_NEURONS][hid] += (LEARN_RATE * errh[hid]); // Update the bias.
        } // hid
        return;
    }

    private static void assignRandomWeights()
    {
        for(int inp = 0; inp <= INPUT_NEURONS; inp++) // Do not subtract 1 here.
        {
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                // Assign a random weight value between -0.5 and 0.5
                wih[inp][hid] = new Random().nextDouble() - 0.5;
            } // hid
        } // inp

        for(int hid = 0; hid <= HIDDEN_NEURONS; hid++) // Do not subtract 1 here.
        {
            for(int out = 0; out < OUTPUT_NEURONS; out++)
            {
                // Assign a random weight value between -0.5 and 0.5
                who[hid][out] = new Random().nextDouble() - 0.5;
            } // out
        } // hid
        return;
    }

    private static double sigmoid(final double val)
    {
        return (1.0 / (1.0 + Math.exp(-val)));
    }

    private static double sigmoidDerivative(final double val)
    {
        return (val * (1.0 - val));
    }
    
    private static void CSVToLearningInput()
    {
    	BufferedReader textReader =null;
        try
		{
        	textReader  = new BufferedReader(new FileReader("input.csv"));
			try
			{
	        	String readedString = ""; // 읽어온 문장을 저정하는 변수입니다. 
	        	String beforeString= "";
				BufferedWriter textWriter = new BufferedWriter(
						new FileWriter("transedOutput.txt", false)); // output.txt 로 저장하기 위해서 writer를 생성합니다.
				while ((readedString = textReader.readLine()) != null) // 파일에서 한줄을 읽어옵니다.
				{
					String[] splitedArray = readedString.split(","); // 읽어온 한줄을 " " 공백으로 분리해 줍니다. 분리하면 문장이 단어들로 변합니다.
					String newOutputString = "";
					int[] numberColor = {0,0,0,0,0};
					for(int i = 0 ; i< splitedArray.length;i++)
					{
						if( i == 0)
						{	
							newOutputString += splitedArray[i];
						}
						else
						{
							newOutputString += ","+splitedArray[i];
						}
						
						if(Integer.parseInt(splitedArray[i]) <= 10)
						{
							numberColor[0]++;
						}
						else if(Integer.parseInt(splitedArray[i]) <= 20)
						{
							numberColor[1]++;
						}
						else if(Integer.parseInt(splitedArray[i]) <= 30)
						{
							numberColor[2]++;
						}
						else if(Integer.parseInt(splitedArray[i]) <= 40)
						{
							numberColor[3]++;
						}
						else if(Integer.parseInt(splitedArray[i]) <= 45)
						{
							numberColor[4]++;
						}
					}
					
					int indexOfBigColor = 0;
					int indexOfSubBigColor = -1;
					int tempIndexOfBigColor = 0;
					for(int i = 0 ; i < numberColor.length;i++)
					{
						if(numberColor[i] > numberColor[tempIndexOfBigColor])
						{
							tempIndexOfBigColor = i;
						}
						if(i == numberColor.length-1)
						{
							if(indexOfSubBigColor == -1)
							{
								indexOfBigColor = tempIndexOfBigColor;
								numberColor[indexOfBigColor] = 0;
								indexOfSubBigColor = 0;
								i = 0;
							}
							else
							{
								indexOfSubBigColor = tempIndexOfBigColor;
							}
						}
					}
					
					newOutputString += "," + indexOfBigColor + "," + indexOfSubBigColor;
					
					String[] tempSplitedString = beforeString.split(",");
					String tempString = "";
					for(int i = 0 ; i < tempSplitedString.length-1;i++)
					{
						tempString += ","+tempSplitedString[i];
					}
					if(beforeString != "")
					{
					textWriter.write(newOutputString+tempString); // 불러온 string을 저장합니다.
					textWriter.newLine(); // 줄을 저장할때마다 한줄을 저장힙니다.
					textWriter.flush();
					}
					beforeString = readedString;
				}
				textWriter.close();
				textReader.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 인풋 파일을 읽어옵니다.
    }
    
    public static void main(String[] args)
    {
    	CSVToLearningInput();
    	LoadTrainingSet();
        NeuralNetwork();
        AutoNumber();
        return;
    }
}