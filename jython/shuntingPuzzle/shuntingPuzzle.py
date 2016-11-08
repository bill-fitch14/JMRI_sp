# Map a sensor state to a specified Function on a specified loco address
#
# used at MWOT to let visitors control the horn and lights on locos, via fascia buttons 
#
# Author: Steve Todd, copyright 2014
# Part of the JMRI distribution
#
from inspect import getargspec
import java
from java.lang import Thread, InterruptedException  
from telnetlib import theNULL
import time

from adaptionRoutines import AdaptionEngine
from adaptionRoutines import AdaptionSim
import jmri
from myscene import listenerObjects
from mytrack import U4_Constants, E1_TNodeNames
from org.apache.log4j import Logger
from sm2 import Main
from sm2 import E1


# import aaSetupSensors
# class EngAdaption(EngineAdaption):
#    
#       filename = 'srimMatrix'
#       adaptors = None
#     #   
#       def __init__(self, adaptors):
#         self.noBoxes = 10
#         self.adaptors = adaptors
#         EngineAdaption(self.noBoxes)
#        
#       def processAdaption(self, millis , distancetravelled, enginesetting, direction):
#         self.adaptors1 = processEngineMeasurement(millis , distancetravelled, enginesetting, direction)
# 
#        
#       def getAdaptors(self):
#         return adaptors
DEBUG = False        
def dprint(*args):
    if (DEBUG):
        for arg in args:
            print(arg),
        print("")
  
LDEBUG = False       
def lprint(*args):
    if (LDEBUG):
        for arg in args:
            print(arg),
        print("")    
  
MDEBUG = False        
def mprint(*args):
    if (MDEBUG):
        for arg in args:
            print(arg),
        print("")
        
# Define one sensor listener. 
class ThrottleFunctionForSensorListener(java.beans.PropertyChangeListener):
  automaton = None  # make a spot to remember the throttle object that was passed in


  def setup(self, throttleAddress):
    th = Automaton()
    th.start()
    #   th.setup("MS-N257E3", 3, "F1")  #horn for address 909
    self.automaton = th
#   if (self.automaton == None) : 
#     #dprint( 'self.throttle is None in setup')
#   else:
#     #dprint( 'self.throttle is not None')
#   return
#   fwd1 = aaSetupSensors("fwd1")
#   fwd1.run()
  
  
  
  
  def propertyChange(self, event):
  
    mprint("  entered property change")
    mprint("propertyName" + event.propertyName)
    mprint("oldVal", event.oldValue, "newVal", event.newValue)
    if event.propertyName == "property1" and event.newValue == 0: 
        turnouts.provideTurnout("MT+10").setState(CLOSED)
        mprint("turnouts.provideTurnout(MT+10).setState(CLOSED)") 
    elif event.propertyName == "property1" and event.newValue == 1:
        turnouts.provideTurnout("MT+10").setState(THROWN)
        mprint("turnouts.provideTurnout(MT+10).setState(THROWN)")
    elif event.propertyName == "property2" and event.newValue == 0:
        turnouts.provideTurnout("MT+20").setState(CLOSED)
        mprint("turnouts.provideTurnout(MT+20).setState(CLOSED)")
    elif event.propertyName == "property2" and event.newValue == 1:
        turnouts.provideTurnout("MT+20").setState(THROWN)
        mprint("turnouts.provideTurnout(MT+20).setState(THROWN)")

    elif event.propertyName == "locoSetDirection" and (event.newValue == 0 or event.newValue == 1) : 
        mprint("Set Loco Forward")
        if (self.automaton == None) : 
          dprint('automation is None')
        self.automaton.setDirection(event.newValue)
  
    elif event.propertyName == "locoSetSpeed" and event.newValue >= 0 and event.newValue <= 1 :
        mprint('SetSpeed')
        self.automaton.setSpeed(event.newValue)
    elif event.propertyName == "loco1SetSpeedZero" and event.newValue >= 0 and event.newValue <= 1 :
        mprint('SetSpeedZero' + str(event.newValue))
        self.automaton.setSpeed(event.newValue)
#     elif event.propertyName == "loco1F" and event.newValue > 0 and event.newValue <= 1 : 
#         mprint("Set Loco Forward event.newValue = " + str(event.newValue))
#         if (self.automaton == None) : 
#             dprint('automation is None')
#         self.automaton.moveLoco(True, event.newValue)
#     elif event.propertyName == "loco1R" and event.newValue > 0 and event.newValue <= 1 :
#         mprint("Set loco Reverse event.newValue = " + str(event.newValue))
#         self.automaton.moveLoco(False, event.newValue)
    elif event.propertyName == "pause" and event.newValue >= 0 and event.newValue <= 10000 :   
        mprint('event.propertyName == "pause"')
        self.automaton.pause(0)
    return    

class Automaton(jmri.jmrit.automat.AbstractAutomaton) :
  
    q = None
    throttle = None
    
    # perform actions that need to be in a thread, such as loco acquisition 
    def init(self):
        mprint("in init Automaton")
    #       logger.debug("Inside Automaton.init("+self.sensorName+","+str(self.throttleAddress)+","+self.fnKeyName+")")
    # actually attach the sensor to the loco
    #     #dprint( 'throttle address' , self.throttleAddress)
    
        self.throttle = self.getThrottle(3, False)
        
        while self.throttle == None:
           self.waitMsec(1) 
#         self.waitMsec(500)  # give it a chance to happen
        dprint("in init setting speed 0")
        self.setSpeed(0)
    #     if self.throttle==None:
    #       #dprint( 'self.throttle1 is None')
    #     else:
    #       #dprint( 'self.throttle1 is not None')
    
        return  
    
    def moveLoco(self, ForwardReverse, ThrottleValue):
        mprint("move loco ForwardReverse " , ForwardReverse , " ThrottleValue " , ThrottleValue)
    #     self.start()
    #     wait 1 second for engine to be stopped, then set speed
    #     self.waitMsec(2000)         
    #     #dprint( "MoveLoco")
    #     self.waitMsec(2000)         
    # #dpr( " 1Set Speed")
    # #dpr( " 2move Loco Forewared" + "   ThrottleValue" +str(ThrottleValue))
    #     if self.throttle==None:
    #       #dprint( "self.throttle is None")
    #     else:
    #       #dprint( "self.throttle is not None")
    
    #     self.throttle.setSpeedSetting(0) 
    #     self.waitMsec(500) 
    # #dpr( " 3Set Speed finished")
    # #dpr( " 4move Loco Forward" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue))
        self.setDirection(ForwardReverse)
        mprint(" 5MoveLoco2")
        #     # wait 1 second for engine to be stopped, then set speed
        self.waitMsec(1000)         
        mprint(" 6move loco set speed" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" + str(ThrottleValue))
        self.setSpeed(ThrottleValue) 
        mprint("moveloco finished")
        return 
    
    def setSpeed(self, ThrottleValue):
        global engineSpeed  # need to declare as global in each function that assigns to it
        
        
        # wait 1 second for engine to be stopped, then set speed
        #     self.waitMsec(500)         
        mprint("Set Speed")
        mprint(" 1Set Speed" + "   ThrottleValue" + str(ThrottleValue))
        engineSpeed = ThrottleValue
        dprint("in setSpeed. speed = " , engineSpeed)
        self.throttle.setSpeedSetting(ThrottleValue) 
        mprint("Set Speed finished")
        return 
    
    def setDirection(self, direction):
        global engineDirection  # need to declare as global in each function that assigns to it
        # wait 1 second for engine to be stopped, then set speed
        #     self.waitMsec(500)         
        dprint("Set Direction")
        if direction == 0:
            mprint(" set direction" + str(direction) + " (backwards)")
            self.throttle.setIsForward(False)
            engineDirection = "reverse"
           
        elif direction == 1:
            mprint(" set direction" + str(direction) + " (forwards)")
            engineDirection = "forwards"
            mprint("!!!!!!!!!!!!!!!! engine direction", engineDirection)
            self.throttle.setIsForward(True)        
            mprint("Set Direction finished")
        dprint("in setDirection", "engineDirection", engineDirection)
        return 
    
    def pause(self, ThrottleValue):
        mprint("pause ThrottleValue = ", ThrottleValue)
        self.throttle.setSpeedSetting(ThrottleValue)
        return 
####################################################################









class SensorListenerDetectHectors(java.beans.PropertyChangeListener):

    global adapt
    
    timeEngOn1 = None
    timeEngOn2 = None
    revStart = None
    revEnd = None
    
    revTime = None
    speedatplusN257E3 = False
    
    processingSENR = False
    processingMSN257 = False
    processingSENF = False
    processingEngOn1 = False
    

    
    simForStart = None
#   CheckSensors = None  #make a spot to remember the throttle object that was passed in

#   def setup(self,  sensorAddress):
#   th=CheckSensors()
#   th.start(sensorAddress)
# #   th.setup("MS-N257E3", 3, "F1")  #horn for address 909
#   self.CheckSensors = th
#   if (self.automaton == None) : 
#     #dprint( 'self.CheckSensors is None in setup')
#   else:
#     #dprint( 'self.CheckSensors is not None')
#   return
#   #dprint( "About to wait for sensor ", sensorAddress)
#   th.waitForSensor(self, sensorAddress)
#   #dprint( "Setup sensor ", sensorAddress)
  
    def propertyChange(self, event):
        
        self.direction = engineDirection
        
        dprint("  entered property change")
        dprint("  propertyName" + event.propertyName)
        mprint("oldVal", event.oldValue, "newVal", event.newValue)
#   if (event.propertyName == "MS-N257E1" or 
#     event.propertyName == "MS+N257E1" or 
#     event.propertyName == "MS-N257E3" or 
#     event.propertyName == "MS+N257E3" ): 
#     and event.newValue != 0: 
        mprint(event.propertyName , "detected at ", event.newValue , " millis")
        if event.propertyName == "SENR1":
            dprint("propertyName " + event.propertyName, " revStart ", event.newValue)
            self.simRevStart = event.newValue
            self.speedatSENR1 = U4_Constants.simSpeedSetting
            mprint("self.speedatSENR1: ", self.speedatSENR1)
            self.processingSENR = True
            return
        
        if event.propertyName == "SENR2":
            dprint("propertyName " + event.propertyName, " revEnd ", event.newValue)
            self.simRevEnd = event.newValue
            self.speedatSENR2 = U4_Constants.simSpeedSetting
            dprint("self.speedatSENR2: ", self.speedatSENR2)
            self.processSENR2()
            self.processingSENR = False
            return
        
        if event.propertyName == "SENF1":
            dprint("propertyName " + event.propertyName, " timeEngOn1 ", event.newValue)
            self.simForStart = event.newValue
            self.speedatSENF1 = U4_Constants.simSpeedSetting
            mprint("self.speedatSENF1: ", self.speedatSENF1)
            self.processingSENF = True
            return
        
        if event.propertyName == "SENF2":
            dprint("propertyName " + event.propertyName, " timeEngOn2 ", event.newValue)
            self.simForEnd = event.newValue
            self.speedatSENF2 = U4_Constants.simSpeedSetting
            dprint("self.speedatSENF2: ", self.speedatSENF2)
            self.processSENF2()
            self.processingSENF = False
            return
        
#         rev1 = aaSetupSensors("MS-N257E1", "engon1")
#         rev1.run()
#         fwd1 = aaSetupSensors("MS+N257E1", "engoff1")
#         fwd1.run()
#         rev2 = aaSetupSensors("MS-N257E3", "engon2")
#         rev2.run()
#         fwd2 = aaSetupSensors("MS+N257E3", "engoff2")
        
#         if event.propertyName == "MS+N257E3":
#             dprint("propertyName" + event.propertyName, " revStart ", event.newValue)
#             self.revStart = event.newValue
#             self.speedatplusN257E3 = engineSpeed
#             dprint("self.speedatplusN257E3: ", self.speedatplusN257E3)
#             self.processingMSN257 = True
#             if self.processingSENR == False:
#                 dprint("UPDATING TRAIN POSITION ***************************************************")
# #                 self.updateTrainPosition(event.propertyName)
#             return
#         
#         elif event.propertyName == "MS+N257E1":
#             dprint("propertyName" + event.propertyName, " revEnd ", event.newValue)
#             dprint("self.speedatplusN257E3: ", self.speedatplusN257E3)
#             self.revEnd = event.newValue
#             self.processMSN257E1()
#             self.processingMSN257 = False
            

        
        if event.propertyName == "engOn1":  # timeEngOn1
            dprint("propertyName" + event.propertyName, " timeEngOn1 ", event.newValue)    
            self.timeEngOn1 = event.newValue
            self.speedatEngOn1 = engineSpeed
            dprint("self.speedatEngOn1: ", self.speedatEngOn1, "self.direction", self.direction)
            # self.processingEngOn1 = True
            if self.direction == 'reverse': 
                if (self.timeEngOn1 > self.timeEngOn2):
                    lprint("ProcessingEngOn1", "self.direction", "self.direction", self.direction, "self.speedatEngOn1", self.speedatEngOn1, "self.timeEngOn1", self.timeEngOn1, "self.timeEngOn2", self.timeEngOn2)
                    self.processEngOn1()
                    self.processingEngOn1 = False
                    self.processingEngOn2 = False
                else:
                    lprint("Not ProcessingEngOn1", "self.direction", self.direction, "self.speedatEngOn1", self.speedatEngOn1, "self.timeEngOn1", self.timeEngOn1, "self.timeEngOn2", self.timeEngOn2)
            else:
                lprint("Not ProcessingEngOn1 - waiting for engOn2")
            if self.processingSENF == False:
                dprint("UPDATING TRAIN POSITION ***************************************************")
        
        elif event.propertyName == "engOn2":  # timeEngOn2
            dprint("propertyName" + event.propertyName, " timeEngOn2 ", event.newValue)
            
            # dprint("self.speedatEngOn1: ", self.speedatEngOn1)
            self.timeEngOn2 = event.newValue
            self.speedatEngOn2 = engineSpeed
            # self.processingEngOn2 = True
            dprint("self.speedatEngOn2: ", self.speedatEngOn2, "self.direction", self.direction)
            if self.direction == 'forwards': 
                if (self.timeEngOn2 > self.timeEngOn1):
                    lprint("ProcessingEngOn2", "self.direction", self.direction, "self.speedatEngOn2", self.speedatEngOn2, "self.timeEngOn1", self.timeEngOn1, "self.timeEngOn2", self.timeEngOn2)
                    self.processEngOn2()
                    self.processingEngOn1 = False
                    self.processingEngOn2 = False
                else:
                    lprint("Not ProcessingEngOn2", "self.direction", self.direction, "self.speedatEngOn2", self.speedatEngOn1, "self.timeEngOn2", self.timeEngOn2)
            else:
                lprint("Not ProcessingEngOn2 - waiting for engOn1")
                

#                 self.updateTrainPosition(event.propertyName)
            return    
            if self.processingSENR == False:
                 dprint("UPDATING TRAIN POSITION ***************************************************")
#                 self.updateTrainPosition(event.propertyName)
#             return
        else:
        #         dprint( "propertyName" + event.propertyName, " newValue ", event.newValue)
        #         dprint( "stopping not calling adaption")
            return
        
    def updateTrainPosition(self, propertyName):
         
        if propertyName == "MS+N257E3":
            dprint("Updating Train Position")
            E1.threads.getModelSetup().get_modelArcAndNodeLinkedList().getTrainsOnRoute().updateToNextSensor()
            
    def processSENF2(self):
        dprint("Hi in processSENF2")
        if self.simForStart == None:
#             self.simForEnd = None
            dprint("self.ForStart is ", self.simForStart, " returning")
            return
        
        if self.simForEnd == None:
            dprint("self.ForStart is ", self.simForEnd, " returning")
            return
        
        self.simForTime = long(self.simForEnd) - long(self.simForStart)
            
        dprint("self.simForEnd", self.simForEnd, "self.simForStart", self.simForStart, "For time taken" , self.simForTime)
        
        self.simDistancetravelled = 227
        dprint("self.simDistancetravelled", self.simDistancetravelled, "self.speedatSENR1", self.speedatSENR1, "self.speedatSENR2", self.speedatSENR1)
        
        #===========================================================================
        # these global values have been set other rotines
        #===========================================================================
        self.engineSetting = engineSpeed
        self.direction = engineDirection
        
        self.min = 0
        self.max = 100
        
        dprint("SENF2", "direction", self.direction)
#         if self.direction == 'forwards':
        adaptModelForward.processMeasurement(
                    self.simForTime , self.simDistancetravelled, self.speedatSENR2, self.min, self.max)
#         else:
#             adaptModelReverse.processMeasurement(
#                     self.simForTime , self.simDistancetravelled, self.speedatSENR2, self.min, self.max)
        self.timeEngOn1 = None
        self.timeEngOn2 = None
        
        return           
    
    def processSENR2(self):
        dprint("Hi in processSENR2")
        if self.simRevStart == None:
#             self.simRevEnd = None
            dprint("self.revStart is ", self.simRevStart, " returning")
            return
        
        if self.simRevEnd == None:
            dprint("self.revStart is ", self.simRevEnd, " returning")
            return
        
        self.simRevTime = long(self.simRevEnd) - long(self.simRevStart)
            
        dprint("self.simRevEnd", self.simRevEnd, "self.simRevStart", self.simRevStart, "rev time taken" , self.simRevTime)
        
        self.simDistancetravelled = 227
        dprint("self.simDistancetravelled", self.simDistancetravelled, "self.speedatSENR1", self.speedatSENR1, "self.speedatSENR2", self.speedatSENR1)
        
        #===========================================================================
        # these global values have been set in other routines
        #===========================================================================
        self.engineSetting = engineSpeed
        self.direction = engineDirection
        
        self.min = 0
        self.max = 100
        dprint("SENR2", "direction", self.direction)
#         if self.direction == 'forwards':
#             adaptModelForward.processMeasurement(
#                     self.simRevTime , self.simDistancetravelled, self.speedatSENR2, self.min, self.max)
#         else:
        adaptModelReverse.processMeasurement(
                    self.simRevTime , self.simDistancetravelled, self.speedatSENR2, self.min, self.max)
        
        self.revStart = None
        self.revEnd = None
        
        return   

    def processMSN257E1(self):
        if self.revStart == None:
            self.revEnd = None
            dprint("self.revStart is ", self.revStart, " returning")
            return
        
        self.revTime = long(self.revEnd) - long(self.revStart)
            
        mprint ("rev time taken" , self.revTime)
        self.distancetravelled = 227
        
        #===========================================================================
        # these global values have been set in other routines
        #===========================================================================
        self.engineSetting = engineSpeed
        self.direction = engineDirection
        
        self.min = 0
        self.max = 1
    
#         if self.direction == 'forwards':
#             adaptEngineForward.processMeasurement(
#                                     self.revTime , self.distancetravelled, self.speedatplusN257E3, self.min, self.max)
#         else:
        adaptEngineReverse.processMeasurement(
                                self.revTime , self.distancetravelled, self.speedatplusN257E3, self.min, self.max)
        
        self.revStart = None
        self.revEnd = None
        
        return
     
    def processEngOn1(self):
        if self.timeEngOn2 == None:
            self.timeEngOn1 = None
            dprint("self.timeEngOn2 is ", "None", " returning")
            return
        
        self.forTime = long(self.timeEngOn1) - long(self.timeEngOn2)
        
        if (self.speedatEngOn1 != self.speedatEngOn2):
            dprint("speed at both sensors is not the same so can't process")
            return
            
        mprint ("for time taken" , self.forTime)
        self.distancetravelled = 227
        
        #===========================================================================
        # these global values have been set in other routines
        #===========================================================================
        # self.engineSetting = engineSpeed (not needed)
        # self.direction = engineDirection (done already)
        
        self.min = 0
        self.max = 1
    
        if self.direction == 'reverse':
            adaptEngineReverse.processMeasurement(
                                self.forTime , self.distancetravelled, self.speedatEngOn1, self.min, self.max)
        else:
#             adaptEngineForward.processMeasurement(
#                                 self.forTime , self.distancetravelled, self.speedatEngOn1, self.min, self.max)
            print "problem trying to adapt at engOn1 in forwards direction"

            
        
        self.timeEngOn1 = None
        self.timeEngOn2 = None
        
        return
      
    def processEngOn2(self):
        if self.timeEngOn1 == None:
            self.timeEngOn2 = None
            dprint("self.timeEngOn1 is ", "None", " returning")
            return
        
        self.forTime = long(self.timeEngOn2) - long(self.timeEngOn1)
        
        if (self.speedatEngOn1 != self.speedatEngOn2):
            dprint("speed at both sensors is not the same so can't process")
            return
            
        mprint ("for time taken" , self.forTime)
        self.distancetravelled = 227
        
        #===========================================================================
        # these global values have been set in other routines
        #===========================================================================
        self.engineSetting = engineSpeed
        self.direction = engineDirection
        
        self.min = 0
        self.max = 1
    
        if self.direction == 'forwards':
            adaptEngineForward.processMeasurement(
                                self.forTime , self.distancetravelled, self.speedatEngOn1, self.min, self.max)
        else:
#             adaptEngineReverse.processMeasurement(
#                                 self.forTime , self.distancetravelled, self.speedatEngOn1, self.min, self.max)
            print "Problem trying to adapt at engOn2 in reverse direction"
        
        self.timeEngOn1 = None
        self.timeEngOn2 = None
        
        return   

logger = Logger.getLogger("jmri.jmrit.jython.exec.ThrottleFunctionForSensor")

class CheckSensors(jmri.jmrit.automat.AbstractAutomaton) :
    sensor = None
    sensorAddress = None
    global jlo
    
    RDEBUG = True        
    def rprint(*args):
        if (RDEBUG):
            print(sensorAddress, ": "),
            for arg in args:
                print(arg),
            print("") 
    
    def __init__(self, sensorAddress, sensorName):
        mprint("Inside init(CheckSensorRev) sensor Address is: ", sensorAddress)
        self.sensorAddress = sensorAddress
        self.sensorName = sensorName
    
    def init(self):
      # init() is called exactly once at the beginning to do
      # any necessary configuration.

      # set up sensor numbers
      # fwdSensor is reached when loco is running forward
        while self.sensorAddress == None:
            time.sleep(1)
        mprint("In CheckSensorsInit, self.sensorAddress=None")
        self.sensor = sensors.provideSensor(self.sensorAddress)
#       self.revSensor2 = sensors.provideSensor("MS-N257E3")
#       self.revSensor1 = sensors.provideSensor("MS-N257E1")
#       self.fwdSensor2 = sensors.provideSensor("MS+N257E3")
#       self.fwdSensor1 = sensors.provideSensor("MS+N257E1")
#       waitForSensor(self.sensor)
        mprint("end of init(CheckSensorRev)")
      
        return
  
    def handle(self):
      
        mprint("waitForSensor YippowaitForSensor", self.sensorAddress)
        while self.sensorAddress == None:
            time.sleep(1)
            mprint("In waitForSensor, self.sensorAddress=None")
        while self.sensor == None:
            mprint(self.sensorAddress , " self.sensor == None")
            self.sensor = sensors.provideSensor(self.sensorAddress)
            time.sleep(10)
        mprint(self.sensorAddress , "self.sensor != None")
        mprint("About to wait for sensor ", self.sensorAddress)
        self.waitSensorActive(self.sensor)
        mprint("Sensor detected ", self.sensorAddress)
        mprint("YippeewaitedForSensor", self.sensorAddress)
        millis = int(round(time.time() * 1000))
        #       if jlo == None:
        #         #dprint( "jlo is null")
        #       else:
        #         #dprint( "jlo is not null")
        #         #dprint( dir(jlo))
        jlo.setSensor(self.sensorAddress, self.sensorName, millis)
        print(self.sensorName, self.sensorAddress, "sensor detected", millis, "engine direction", engineDirection)
        self.waitSensorInactive(self.sensor)
        #       c = Main.lo.setSensor(self.sensorAddress,millis)
        # handle() is called repeatedly until it returns false.
        mprint("Inside handle(CheckSensorRev)")
        return 1  
        # (requires JMRI to be terminated to stop - caution

    
      
         
#     def waitForReverseSensor1(self):
#       #dprint( "YippowaitForReverseSensor1")
#       
#       while self.revSensor1 == None:
#         if self.revSensor1 == None:
#           #dprint( "self.revSensor == None")
#           self.revSensor1 = sensors.provideSensor("MS-N257E1")
#           time.sleep(10)
#         else:
#           #dprint( "self.revSensor != None")
#       #dprint( "self.revSensor != None")
#       self.waitSensorActive(self.revSensor1)
#       #dprint( "YippeewaitForReverseSensor1")
#       
#     def waitForReverseSensor2(self):
#       #dprint( "YippowaitForReverseSensor2")
#       
#       while self.revSensor2 == None:
#         if self.revSensor2 == None:
#           #dprint( "self.revSensor == None")
#           self.revSensor1 = sensors.provideSensor("MS-N257E3")
#           time.sleep(10)
#         else:
#           #dprint( "self.revSensor != None")
#       #dprint( "self.revSensor != None")
#       self.waitSensorActive(self.revSensor2)
#       #dprint( "YippeewaitForReverseSensor2")
#       
#     def waitForForwardSensor1(self):
#       #dprint( "YippowaitForForwardSensor1")
#       
#       while self.fwdSensor1 == None:
#         if self.fwdSensor1 == None:
#           #dprint( "self.revSensor == None")
#           self.fwdSensor1 = sensors.provideSensor("MS+N257E1")
#           time.sleep(10)
#         else:
#           #dprint( "self.revSensor != None")
#       #dprint( "self.revSensor != None")
#       self.waitSensorActive(self.fwdSensor1)
# #       t.addPropertyChangeListener(self.fwdSensor1)
# #       Main.lo.addPropertyChangeListener(self.fwdSensor1)
#       #dprint( "YippeewaitForForwardSensor1")
#       
#     def waitForForwardSensor2(self):
#       #dprint( "YippowaitForForwardSensor2")
#       
#       while self.fwdSensor2 == None:
#         if self.fwdSensor2 == None:
#           #dprint( "self.revSensor == None")
#           self.fwdSensor2 = sensors.provideSensor("MS+N257E3")
#           time.sleep(10)
#         else:
#           #dprint( "self.revSensor != None")
#       #dprint( "self.revSensor != None")
#       self.waitSensorActive(self.fwdSensor2)
#       #dprint( "YippeewaitForForwardSensor2")
  
  # end of class definition
class aaSetupSensors(Thread):
  
#   r = SensorListenerDetectHectors()
#   r.setup()
  
  
     
    t = None
    sensorAddress = None
    
    def __init__(self, sensorAddress, sensorName):
        mprint("aaSetupSensorsInside init(SetupSensors)")
        self.sensorAddress = sensorAddress
        self.sensorName = sensorName
        mprint("aaSetupSensors inside init(SetupSensors)", sensorAddress)
    
    def run(self):
      
      
        while self.sensorAddress == None:
           self.currentThread().sleep(1000) 
           mprint('aaSetupSensors waiting for sensoraddress')
           mprint("aaSetupSensors sensorAddress should not be none", self.sensorAddress)
        self.t = CheckSensors(self.sensorAddress, self.sensorName).start()

# 
#     dprint( "Inside run(SetupSensors)")
#     while self.t == None:
#       #dprint( "Waiting t is None")
#       self.currentThread().sleep(1000)
#     #dprint( "t is not None")
#     while not self.currentThread().isInterrupted():
#       #dprint( "in thread")
#       self.t.waitForSensor()
# #       
# #       if self.sensor == "fwd1":
# #         self.t.waitForForwardSensor1()
# #       elif self.sensor == "fwd2":
# #         self.t.waitForForwardSensor2()
# #       elif self.sensor == "rev1":
# #         self.t.waitForReverseSensor1()
# #       elif self.sensor == "rev2":
# #         self.t.waitForReverseSensor2()
#       #dprint ("Sensor detected")
#       try: 
#         a=1
# #         self.currentThread().sleep(5000)
#       except InterruptedException: 
#         self.interrupt()     


# foo.start()

# l = MyLocoListener()
global jlo
global engineSpeed 
global engineDirection
engineSpeed = -99
engineDirection = "xxxx"
# connect each sensor to its loco and function, repeat as needed
# r=Automaton()
# s=r.setup("MS-N257E3", 3, "F1")  #horn for address 909
global adapt
initialise = True
noBoxes = 10
forgettingfactor = .99
adaptEngineForward = AdaptionEngine(noBoxes, "EngineForwardAdaption", initialise, forgettingfactor)

initialise = False
adaptEngineReverse = AdaptionEngine(noBoxes, "EngineReverseAdaption", initialise, forgettingfactor)

initialise = False
forgettingfactor = .999
adaptModelForward = AdaptionSim(noBoxes, "ModelForwardAdaption", initialise, forgettingfactor)
initialise = False
adaptModelReverse = AdaptionSim(noBoxes, "ModelReverseAdaption", initialise, forgettingfactor)

q = ThrottleFunctionForSensorListener()
q.setup(3)
# r.setupThrottleFunction(q)


Main.lo.addPropertyChangeListener(q)
Main.runFSM()

jlo = listenerObjects()
r = SensorListenerDetectHectors()
# r.setup()
jlo.addPropertyChangeListener(r)


rev1 = aaSetupSensors("MS-N257E1", "engOn1")
rev1.run()
fwd1 = aaSetupSensors("MS+N257E1", "engOff1")
fwd1.run()
rev2 = aaSetupSensors("MS-N257E3", "engOn2")
rev2.run()
fwd2 = aaSetupSensors("MS+N257E3", "engOff2")
fwd2.run()
# fwd1.run()
# rev1 = aaSetupSensors("rev1")
# rev1.run()
# fwd2 = aaSetupSensors("fwd2")
# fwd2.run()
# rev2 = aaSetupSensors("rev2")
# rev2.run()
