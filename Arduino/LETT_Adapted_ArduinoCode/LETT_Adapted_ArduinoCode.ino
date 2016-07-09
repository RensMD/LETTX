// Writen by Pieter Welling november 6th 2014 for graduation project: Design of a desktop tensile tester
// Adapted by Rens Doornbusch, June 2016. Adding Compression test functionality, Control Buttons and cross platform support of application.


// Library used for the analog to digital converter
#include "Hx711.h"
#include <digitalWriteFast.h>

// Declare port 1 and 2 on the Arduino board
Hx711 scale(A1, A0);

// LETT dependent variables. Check Excel sheet for right values.
int LETTNumber = 19;
float gain100 = 0.466;
float gain500 = 2.239;

//Miscelaneous variables
int incomingByte;
int oldByte;
int numberOfSteps = 0;
int Speed = 0;
boolean testStarted = false;
boolean startReceived = false;
boolean dataReceived = false;

//Actuator control variables                                                     
int pwmSignal = 0;
int actuatorDown = 3;
int actuatorUp = 4;
int actuatorSpeed = 5;

//Load cell variables
float loadCellGain = 1;
boolean loadCellRead = false;
boolean loadCell100 = false;
boolean loadCell500 = false;
const int time = 8;
int reset = 12;

//Digital ruler variables 
boolean digitalRulerRead = false;
volatile int count=0;                                                                     
volatile int data[48];                                                                    
int numberOfDigits = 0;   
long reading = 0;
const float conv_fact = 806.299212;
float compensatie=0;

//Distance variables
float absolutDistance =0;
float absolutDistanceOneStepAgo = 0;
float absolutDistanceTwoStepAgo = 0;
float absolutDistanceThreeStepAgo = 0;
float relativeDistance =0;
float relativeDistanceOneStepAgo = 0;
float relativeDistanceTwoStepAgo = 0;

// Speed variables
float velocity=0;
float velocityBeforeCorrection = 0;
boolean moveWith10 = false;
boolean moveWith20 = false;
boolean moveWith50 = false;
boolean moveWith100 = false;

//Force variables                                                                                           
float force=0;
float forceValueAtStart = 0;
float forceTwoStepAgo = 20;
float forceOneStepAgo = 20;

// test type variables
boolean tensionTest = false;
boolean compressionTest = false;

// Time variables                                                                                          
unsigned long timeAtStart = 0;
unsigned long timeSinceStart = 0;
unsigned long timeSincesLastStep = 0;
unsigned long timeAtLastStep = 0;

char start_char = '@';
char end_char = '#';
char sep_char = ':';
String temp;


void setup(){                                                                                      
  pinMode(actuatorUp,OUTPUT);                                                              
  pinMode(actuatorDown,OUTPUT);
  analogWrite(actuatorSpeed,0);
                                                                                          
  pinModeFast(time, INPUT);  
  pinMode(reset, OUTPUT);  
  digitalWrite(12,HIGH);
  attachInterrupt(0,clock,FALLING);                                                 
  
  Serial.begin(19200);
}

void loop(){                                                                             
  //Read signal from computer
  if (Serial.available() > 0) {                                                          
    incomingByte = Serial.read();
  }
  
  if (!testStarted){
    //Check to move top grip up
    if(incomingByte == 'A') {
      topGripUp_ButtonDown();
    }
    //Check to stop moving top grip up
    else if (incomingByte == 'H') {
      topGripUp_ButtonUp();
    } 
    //Check to move top grip adown
    else if(incomingByte == 'B') {
      topGripDown_ButtonDown();
    } 
    //Check to stop moving top grip down
    else if(incomingByte == 'G') {
      topGripDown_ButtonUp();
    }
    //Check if speed is set
    else if (incomingByte == '1' || incomingByte == '2' || incomingByte == '3' || incomingByte == '4') {
      setSpeed();
    } 
    //Check load cell gain value
    else if (incomingByte == 'E' || incomingByte == 'F') {
      setLoadCell();
    } 
    //check load cell gain value
    else if (incomingByte == 'T' || incomingByte == 'R') {
      setTestType();
    } 
    
    else if(incomingByte == 'O') {
      startReceived=true;
    }
    //Start a test
    else if (incomingByte == 'I') {
      startTest();
    }
    //Reset incomingbyte value
    incomingByte=' ';

  startStream();
  writeStream(LETTNumber);
  endStream();


//      if(n=20){
//      //Write LETT number to UI
//      Serial.print("startL");
//      Serial.print('\n');
//      Serial.print(LETTNumber);
//      Serial.print('\n');
//      Serial.print("stop");
//      Serial.print('\n');
//      write("start");
//      Serial.flush();
//    }
//    n++;

//    if(!startReceived){
//      //Write LETT number to UI
//      Serial.print("startL");
//      Serial.print('\n');
//      Serial.print(LETTNumber);
//      Serial.print('\n');
//      Serial.print("stop");
//      Serial.print('\n');
//    }
  }
  else{
    //Check for stop signal
    if (incomingByte == 'C'){
      STOP();
      incomingByte=' ';
    }
  
    //Read value from digital ruler
    digitalruler();                                                                      
    
    //Read value from load cell
    loadcell();                                                                          
    
    //Check time passed since last step
    timeSincesLastStep = millis() - timeAtLastStep;
    
    //Check if time to write values to computer and adjust speed and check if needed to stop test
    if (millis() < 200 || digitalRulerRead && loadCellRead && timeSincesLastStep > 300){
      
      //Calculate speed since last step
      calculateSpeed();
      
      //Set speed to 10 mm/min
      if (moveWith10){
        tenMillimeter();
      }
      //Set speed to 20 mm/min
      else if (moveWith20){
        twentyMillimeter();
      }
      //Set speed to 50 mm/min
      else if (moveWith50){
        fiftyMillimeter();
      }
      //Set speed to 100 mm/min
      else if (moveWith100){
        hundredMillimeter();
      }
      
      //Check if force is bigger than froce max allowed
      checkForceMax();
      
      //Stop test at failing of specimen
      stopAtFail();
      
      //Write values to UI
      writevalues();  
  
      //Reset values for next step  
      resetVariablesForNextStep();    
    }
  }  
}

//Starts a test 
void startTest(){                                                             
  //Reset digital ruler
  digitalWrite (reset,LOW);
  delay(400);
  digitalWrite (reset,HIGH);
  
  //Set direction of movement
  if(tensionTest){
    digitalWrite(actuatorDown,LOW);
    digitalWrite(actuatorUp,HIGH);
  }
  else if(compressionTest){
    digitalWrite(actuatorDown,HIGH);
    digitalWrite(actuatorUp,LOW);
  }
  
  //Indicate test has started
  testStarted = true;
  
  //Reset test variables
  timeAtStart = millis();
  timeAtLastStep = millis();
  numberOfSteps = 0;
  absolutDistanceOneStepAgo = 0;
  forceOneStepAgo =0;
  
  //Set PWM signal to correct value for selected speed and set speed value  
  if (moveWith100){
    pwmSignal = 110;
    compensatie = 0.2;
    velocity = 0.0015;
  } else if (moveWith50){
    pwmSignal = 80;
    compensatie=0.1;
    velocity = 0.0008;
  } else if (moveWith20){
    pwmSignal = 60;
    compensatie = 0.04;
    velocity = 0.0032;
  } else if (moveWith10){
    pwmSignal = 40;
    compensatie = 0.02;
    velocity = 0.00016;
  }
  //Write PWM signal value to PWM port
  analogWrite(actuatorSpeed,pwmSignal);
}

//Write values to computer
void writevalues(){  
  //Write values to UI
  //String combineddata="startD"+absolutDistance+"-"+force+"-"+timeSinceStart+"stop";
  
  //Serial.print(combineddata);
  
// TODO: Solution with receive check
//  oldByte = incomingByte;
//  while(incomingByte == oldByte){
//    //Write values to UI
//    Serial.print("data");                                             
//    Serial.print('\n');
//    Serial.print(absolutDistance);                                                    
//    Serial.print('\n');
//    Serial.print(force);          
//    Serial.print('\n');  
//    Serial.print(timeSinceStart);          
//    Serial.print('\n');
//    delay(10);
//    if (Serial.available() > 0) {                                                          
//      incomingByte = Serial.read();
//    }
//  }

  Serial.print("startD");  
  Serial.print('\n');
  Serial.print(absolutDistance);                                                    
  Serial.print('\n');
    
  Serial.print(force);          
  Serial.print('\n');
         
  Serial.print(timeSinceStart);          
  Serial.print('\n');
  Serial.print("stop");
}

/*----- Read Data Functions -----*/
//Read distance value from digital ruler
void digitalruler(){  
  reading = 0;
  boolean negative = false;
  while(count>47){                                                                      // wait until we have all 48 data bits
    detachInterrupt(0);                                                                 // override interrupts whilst processing
    negative=data[47];                                                                  // check for a negative number
    for(numberOfDigits=24; numberOfDigits<48; numberOfDigits++){                        // ingore redundant first 24 bits
      bitWrite(reading,numberOfDigits-24,data[numberOfDigits]^negative);                // convert bit using 1's complement (if necessary)
    }
    if (negative) {reading=-reading;}                                                   // invert readout if negative

    absolutDistance = (reading / conv_fact);
    count = 0;
    attachInterrupt(0,clock,RISING);                                                    // restart the data-logging
  }
    
    if (absolutDistance < 0){
      absolutDistance*=-1;
    }
    
    //Indicate the ruler is read
    digitalRulerRead = true;
}

void clock(){    
  //Read in data bits up to 48 total  
  if(count<48){
    data[count] = digitalReadFast(time);
    count++;
  }
}

//Read load cell value
void loadcell(){                                                                         
  //Read first force value
  if (numberOfSteps == 0){
    forceValueAtStart = scale.getGram();
  }
  
  //Substract first force value from force value to measure value from zero
  force = ((scale.getGram() - forceValueAtStart) * loadCellGain);
  
  //Adjust unrealistic force value at start of test <step 6
  if (numberOfSteps < 6 && abs(force)>3000){
    force = forceOneStepAgo;
  }
  
  //Indicate laod cell is read       
  loadCellRead = true;
}

//Check if force value is bigger than allowable value
void checkForceMax(){
  if (loadCell100 && (force > 1000)){
    forceSTOP();
  } else if (loadCell500 && (force > 5000)){
    forceSTOP();
  }
}

//Calculate speed of movement
void calculateSpeed(){
  //Set first distance value to zero
  if (numberOfSteps == 0){
    absolutDistance = 0;
  }
  
  if (numberOfSteps < 10 && absolutDistance > 100){
    absolutDistance = (absolutDistanceOneStepAgo + compensatie);
  }
  
  if (numberOfSteps > 9){  
    
    if (absolutDistance > 110){
      absolutDistance = (absolutDistanceOneStepAgo + compensatie);
    }
  }
  
  //Calculate reletivedistance values
  relativeDistanceTwoStepAgo = absolutDistanceTwoStepAgo - absolutDistanceThreeStepAgo;
  relativeDistanceOneStepAgo = absolutDistanceOneStepAgo - absolutDistanceTwoStepAgo;
  relativeDistance = (absolutDistance - absolutDistanceOneStepAgo);
  
  //Calculate speed
  velocity = ((relativeDistance) / timeSincesLastStep);
  
  //Increase number of steps with +1
  numberOfSteps += 1;
  //Calculate time since start of test
  timeSinceStart = millis() - timeAtStart;
}

/*----- Setup Functions -----*/
//Sets speed to either 10, 50 or 100 mm/min
void setSpeed() {
  moveWith10 = incomingByte == '1';
  moveWith20 = incomingByte == '2';
  moveWith50 = incomingByte == '3';
  moveWith100 = incomingByte == '4';
}

//Set gain value of load cell (gain value converts value of load cell to corresponding force value)
void setLoadCell() {
  loadCell100 = incomingByte == 'E';
  loadCell500 = incomingByte == 'F';

  if (incomingByte == 'E'){                                                             
    loadCellGain = gain100;
    loadCell500 =false;
  } else if (incomingByte == 'F'){                        
    loadCellGain = gain500;
    loadCell100 =false;
  }
}

//sets test type to either tension or compression
void setTestType(){
  tensionTest = incomingByte == 'T';
  compressionTest = incomingByte == 'R';
}

/*----- Speed Step Functions -----*/
//Conditions for if movement with 10 mm/min
void tenMillimeter(){  
  //Adjust unrealistic force and distance values > step 5    
   if (numberOfSteps > 10){     
     if (abs(force) > (2 * abs(forceOneStepAgo))){
       force = forceOneStepAgo;
     }
   }
  
  //Increase speed if speed is lower than threshold value                                      
  if (velocity < 0.0001){
    pwmSignal+=2;
  }
  
  //Decrease speed if speed is higher than threshold value  
  if (velocity > 0.0002){
    pwmSignal-=3;
  }
  
  //Write adjusted PWMsignal value to port 'actuatorSpeed'          
  analogWrite(actuatorSpeed,pwmSignal);
}

//Conditions for if movement with 10 mm/min
void twentyMillimeter(){  
  //Adjust unrealistic force and distance values > step 5    
   if (numberOfSteps > 10){     
     if (abs(force) > (2 * abs(forceOneStepAgo))){
       force = forceOneStepAgo;
     }
   }
  
  //Increase speed if speed is lower than threshold value                                      
  if (velocity < 0.0002){
    pwmSignal+=2;
  }
  
  //Decrease speed if speed is higher than threshold value  
  if (velocity > 0.0004){
    pwmSignal-=3;
  }
  
  //Write adjusted PWMsignal value to port 'actuatorSpeed'          
  analogWrite(actuatorSpeed,pwmSignal);
}

//Conditions for if movement with 50 mm/min
void fiftyMillimeter(){
  //Adjust unrealistic force and distance values > step 5    
   if (numberOfSteps > 10){     
     if (abs(force) > (2 * abs(forceOneStepAgo))){
       force = forceOneStepAgo;
     }
   }
     
  //Increase speed if speed is lower than threshold value                                      
  if (velocity < 0.0006){
    pwmSignal+=2;
  }
  
  //Decrease speed if speed is higher than threshold value  
  if (velocity > 0.0007){
    pwmSignal-=3;
  }
  
  //Write adjusted PWMsignal value to port 'actuatorSpeed'          
  analogWrite(actuatorSpeed,pwmSignal);
}

//Conditions for if movement with 100 mm/min
void hundredMillimeter(){
  //Adjust unrealistic force and distance values > step 5    
   if (numberOfSteps > 10){     
     if (abs(force) > (2 * abs(forceOneStepAgo))){
       force = forceOneStepAgo;
     }
   }
  
  //Increase speed if speed is lower than threshold value                                      
  if (velocity < 0.0015){
    pwmSignal+=2;
  }
  
  //Decrease speed if speed is higher than threshold value  
  if (velocity > 0.0016){
    pwmSignal-=3;
  }
  
  //Write adjusted PWMsignal value to port 'actuatorSpeed'          
  analogWrite(actuatorSpeed,pwmSignal);
}

//Ready software for next step 
void resetVariablesForNextStep(){    
  absolutDistanceThreeStepAgo = absolutDistanceTwoStepAgo;
  absolutDistanceTwoStepAgo = absolutDistanceOneStepAgo;
  absolutDistanceOneStepAgo = absolutDistance;
      
  forceOneStepAgo = force;
    
  timeAtLastStep = millis(); 
  digitalRulerRead = false;
  loadCellRead = false;
}

/*----- Control Functions -----*/
//Stop test at failing of specimen
void stopAtFail(){
  //Stop test if force falls after 20 steps
  if (numberOfSteps > 20 && force < 20){
    forceSTOP();   
  }
}  

//Stop test 
void forceSTOP(){
  //Write stop command to UI
  // TODO: wat hier?   
  STOP();
}

//Stops a test and all movement of LETT
void STOP(){
  digitalWrite(actuatorDown,LOW);
  digitalWrite(actuatorUp,LOW);
  analogWrite(actuatorSpeed,0);
  testStarted = false;
  numberOfSteps = 0;
}

//Moves top grip up while button is pressed   
void  topGripUp_ButtonDown(){
  digitalWrite(actuatorDown,LOW);
  digitalWrite(actuatorUp,HIGH);
  analogWrite(actuatorSpeed,255);
}

//Stops moving top grip up when button is released    
void  topGripUp_ButtonUp(){
  digitalWrite(actuatorDown,LOW);
  digitalWrite(actuatorUp,LOW);
  analogWrite(actuatorSpeed,0);
}    

//Moves top grip down while button is pressed   
void  topGripDown_ButtonDown(){
  digitalWrite(actuatorDown,HIGH);
  digitalWrite(actuatorUp,LOW);
  analogWrite(actuatorSpeed,255);
}

//Stops moving top grip down when button is released     
void  topGripDown_ButtonUp(){
  digitalWrite(actuatorDown,LOW);
  digitalWrite(actuatorUp,LOW);
  analogWrite(actuatorSpeed,0);
}

//Methods to Convert Everything to String
//Then Send out through Serial Port
void startStream(){
 Serial.write(start_char);
 Serial.write(sep_char);
 Serial.flush();
}
 
void endStream(){
 Serial.write(end_char);
 Serial.write(sep_char);
 Serial.flush();
}
 
void sepStream(){
 Serial.write(sep_char);
 Serial.flush();
}
 
void writeStream(float data){
 temp = String(data);
 
 byte charBuf[temp.length()];
 temp.getBytes(charBuf,temp.length()+1);
 
 Serial.write(charBuf,temp.length());
 Serial.flush();
 sepStream();
 
}

void writeStream(int data){
 temp = String(data);
 
 byte charBuf[temp.length()];
 temp.getBytes(charBuf,temp.length()+1);
 
 Serial.write(charBuf,temp.length());
 Serial.flush();
 sepStream();
}
 
void writeStream(unsigned long data){
 temp = String(data);
 
 byte charBuf[temp.length()];
 temp.getBytes(charBuf,temp.length()+1);
 
 Serial.write(charBuf,temp.length());
 Serial.flush();
 sepStream();
}
 
void writeStream(char string[]){
 Serial.write(string);
 Serial.flush();
 sepStream();
}
