# Map a sensor state to a specified Function on a specified loco address
#
# used at MWOT to let visitors control the horn and lights on locos, via fascia buttons 
#
# Author: Steve Todd, copyright 2014
# Part of the JMRI distribution
#
from sm2 import Main
from myscene import listenerObjects
import java
import jmri
import time
# import aaSetupSensors

from org.apache.log4j import Logger
from java.lang import Thread, InterruptedException  
from telnetlib import theNULL
    

# Define one sensor listener. 
class ThrottleFunctionForSensorListener(java.beans.PropertyChangeListener):
  automaton = None  #make a spot to remember the throttle object that was passed in

  def setup(self,  throttleAddress):
    th=Automaton()
    th.start()
#     th.setup("MS-N257E3", 3, "F1")  #horn for address 909
    self.automaton = th
    if (self.automaton == None) : 
        print 'self.throttle is None in setup'
    else:
        print 'self.throttle is not None'
    return
    fwd1 = aaSetupSensors("fwd1")
    fwd1.run()
    
    
    
    
  def propertyChange(self, event):
    
    print "    entered property change"
    print "propertyName" + event.propertyName
    print "oldVal", event.oldValue, "newVal", event.newValue
    if event.propertyName == "property1" and event.newValue == 0: 
        turnouts.provideTurnout("MT+10").setState(CLOSED)
        print "turnouts.provideTurnout(MT+10).setState(CLOSED)"        
    elif event.propertyName == "property1" and event.newValue == 1:
        turnouts.provideTurnout("MT+10").setState(THROWN)
        print "turnouts.provideTurnout(MT+10).setState(THROWN)"
    elif event.propertyName == "property2" and event.newValue == 0:
        turnouts.provideTurnout("MT+20").setState(CLOSED)
        print "turnouts.provideTurnout(MT+20).setState(CLOSED)"
    elif event.propertyName == "property2" and event.newValue == 1:
        turnouts.provideTurnout("MT+20").setState(THROWN)
        print "turnouts.provideTurnout(MT+20).setState(THROWN)"
    elif event.propertyName == "loco1F" and event.newValue >0 and event.newValue <=1 : 
        print "Set Loco Forward event.newValue = " + str(event.newValue) 
        if (self.automaton == None) : 
            print 'automation is None'
        self.automaton.moveLoco(True,event.newValue)
    elif event.propertyName == "locoSetDirection" and (event.newValue == 0 or event.newValue == 1) : 
        print "Set Loco Forward"
        if (self.automaton == None) : 
            print 'automation is None'
        self.automaton.setDirection(event.newValue,event.newValue)

    elif event.propertyName == "loco1SetSpeed" and event.newValue >=0 and event.newValue <=1 :
        print 'SetSpeed'
        self.automaton.setSpeed(event.newValue)
    elif event.propertyName == "loco1SetSpeedZero" and event.newValue >=0 and event.newValue <=1 :
        print 'SetSpeedZero' + str(event.newValue)
        self.automaton.setSpeed(event.newValue)
    elif event.propertyName == "loco1R" and event.newValue >0 and event.newValue <=1 :
        print "Set loco Reverse event.newValue = " + str(event.newValue)
        self.automaton.moveLoco(False,event.newValue)
    elif event.propertyName == "pause" and event.newValue >=0 and event.newValue <=10000 :   
        print 'event.propertyName == "pause"' 
        self.automaton.pause(0)
    return        

class Automaton(jmri.jmrit.automat.AbstractAutomaton) :
    
    q = None
    throttle = None
    #perform actions that need to be in a thread, such as loco acquisition 
    def init(self):
        print "in init Automaton"
#             logger.debug("Inside Automaton.init("+self.sensorName+","+str(self.throttleAddress)+","+self.fnKeyName+")")
         # actually attach the sensor to the loco
#         print 'throttle address' , self.throttleAddress

        self.throttle = self.getThrottle(3, False)
        self.waitMsec(500)  #give it a chance to happen
        self.throttle.setSpeedSetting(0)
        if self.throttle==None:
            print 'self.throttle1 is None'
        else:
            print 'self.throttle1 is not None'
        
        return  
       
    def moveLoco(self,ForwardReverse, ThrottleValue):
        print "move loco"
#         self.start()
#         wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(2000)                 
#         print "MoveLoco"
#         self.waitMsec(2000)                 
        print " 1Set Speed"
        print " 2move Loco Forewared" + "   ThrottleValue" +str(ThrottleValue)
        if self.throttle==None:
            print "self.throttle is None"
        else:
            print "self.throttle is not None"
        
#         self.throttle.setSpeedSetting(0) 
#         self.waitMsec(500) 
        print " 3Set Speed finished"
        print " 4move Loco Forward" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setIsForward(ForwardReverse)
        print " 5MoveLoco2"
#         # wait 1 second for engine to be stopped, then set speed
        self.waitMsec(1000)                 
        print " 6move loco set speed" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(ThrottleValue) 
        print "moveloco finished"
        return 
    
    def setSpeed(self,ThrottleValue):

        # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(500)                 
        print "Set Speed"
        print " 1Set Speed" + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(ThrottleValue) 
        print "Set Speed finished"
        return 
    
    def setDirection(self,direction):

        # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(500)                 
        print "Set Direction"
        if direction == 0:
            print " set direction"  +str(direction) + " (backwards)"
            self.throttle.setIsForward(False) 
        elif direction == 1:
            print " set direction"  +str(direction) + " (forwards)"
            self.throttle.setIsForward(True)
        
        print "Set Direction finished"
        return 
    
    def pause(self,ThrottleValue):
        print "pause"
        self.throttle.setSpeedSetting(0)
        return 
####################################################################









class SensorListenerDetectHectors(java.beans.PropertyChangeListener):
#   CheckSensors = None  #make a spot to remember the throttle object that was passed in

#   def setup(self,  sensorAddress):
#     th=CheckSensors()
#     th.start(sensorAddress)
# #     th.setup("MS-N257E3", 3, "F1")  #horn for address 909
#     self.CheckSensors = th
#     if (self.automaton == None) : 
#         print 'self.CheckSensors is None in setup'
#     else:
#         print 'self.CheckSensors is not None'
#     return
#     print "About to wait for sensor ", sensorAddress
#     th.waitForSensor(self, sensorAddress)
#     print "Setup sensor ", sensorAddress
    
  def propertyChange(self, event):
    
    print "    entered property change"
    print "propertyName" + event.propertyName
    print "oldVal", event.oldValue, "newVal", event.newValue
    if (event.propertyName == "MS-N257E1" or 
        event.propertyName == "MS+N257E1" or 
        event.propertyName == "MS-N257E3" or 
        event.propertyName == "MS+N257E3" ): 
#         and event.newValue != 0: 
        print event.propertyName , "detected at ", event.newValue , " millis"
    return   

logger = Logger.getLogger("jmri.jmrit.jython.exec.ThrottleFunctionForSensor")

class CheckSensors(jmri.jmrit.automat.AbstractAutomaton) :
        sensor = None
        sensorAddress = None
        global jlo
        
        def __init__(self,sensorAddress):
            print "Inside init(CheckSensorRev) sensor Address is: ",sensorAddress
            self.sensorAddress = sensorAddress
        
        def init(self):
            # init() is called exactly once at the beginning to do
            # any necessary configuration.

            # set up sensor numbers
            # fwdSensor is reached when loco is running forward
            while self.sensorAddress==None:
                time.sleep(1)
                print "In CheckSensorsInit, self.sensorAddress=None"
            self.sensor = sensors.provideSensor(self.sensorAddress)
#             self.revSensor2 = sensors.provideSensor("MS-N257E3")
#             self.revSensor1 = sensors.provideSensor("MS-N257E1")
#             self.fwdSensor2 = sensors.provideSensor("MS+N257E3")
#             self.fwdSensor1 = sensors.provideSensor("MS+N257E1")
#             waitForSensor(self.sensor)
            print "end of init(CheckSensorRev)"
            
            return
    
        def handle(self):
            
            print "waitForSensor YippowaitForSensor",self.sensorAddress
            while self.sensorAddress==None:
                time.sleep(1)
                print "In waitForSensor, self.sensorAddress=None"
            while self.sensor == None:
                print self.sensorAddress , " self.sensor == None"
                self.sensor = sensors.provideSensor(self.sensorAddress)
                time.sleep(10)
            print self.sensorAddress , "self.sensor != None"
            print "About to wait for sensor ", self.sensorAddress
            self.waitSensorChange(self.sensor)
            print "Sensor detected ", self.sensorAddress
            print "YippeewaitedForSensor", self.sensorAddress
            millis = int(round(time.time() * 1000))
            if jlo == None:
                print "jlo is null"
            else:
                print "jlo is not null"
                print dir(jlo)
            jlo.setSensor(self.sensorAddress, millis)
#             c = Main.lo.setSensor(self.sensorAddress,millis)
            # handle() is called repeatedly until it returns false.
            print "Inside handle(CheckSensorRev)"
            return 1    
            # (requires JMRI to be terminated to stop - caution

        
          
               
#         def waitForReverseSensor1(self):
#             print "YippowaitForReverseSensor1"
#             
#             while self.revSensor1 == None:
#                 if self.revSensor1 == None:
#                     print "self.revSensor == None"
#                     self.revSensor1 = sensors.provideSensor("MS-N257E1")
#                     time.sleep(10)
#                 else:
#                     print "self.revSensor != None"
#             print "self.revSensor != None"
#             self.waitSensorActive(self.revSensor1)
#             print "YippeewaitForReverseSensor1"
#             
#         def waitForReverseSensor2(self):
#             print "YippowaitForReverseSensor2"
#             
#             while self.revSensor2 == None:
#                 if self.revSensor2 == None:
#                     print "self.revSensor == None"
#                     self.revSensor1 = sensors.provideSensor("MS-N257E3")
#                     time.sleep(10)
#                 else:
#                     print "self.revSensor != None"
#             print "self.revSensor != None"
#             self.waitSensorActive(self.revSensor2)
#             print "YippeewaitForReverseSensor2"
#             
#         def waitForForwardSensor1(self):
#             print "YippowaitForForwardSensor1"
#             
#             while self.fwdSensor1 == None:
#                 if self.fwdSensor1 == None:
#                     print "self.revSensor == None"
#                     self.fwdSensor1 = sensors.provideSensor("MS+N257E1")
#                     time.sleep(10)
#                 else:
#                     print "self.revSensor != None"
#             print "self.revSensor != None"
#             self.waitSensorActive(self.fwdSensor1)
# #             t.addPropertyChangeListener(self.fwdSensor1)
# #             Main.lo.addPropertyChangeListener(self.fwdSensor1)
#             print "YippeewaitForForwardSensor1"
#             
#         def waitForForwardSensor2(self):
#             print "YippowaitForForwardSensor2"
#             
#             while self.fwdSensor2 == None:
#                 if self.fwdSensor2 == None:
#                     print "self.revSensor == None"
#                     self.fwdSensor2 = sensors.provideSensor("MS+N257E3")
#                     time.sleep(10)
#                 else:
#                     print "self.revSensor != None"
#             print "self.revSensor != None"
#             self.waitSensorActive(self.fwdSensor2)
#             print "YippeewaitForForwardSensor2"
    
    # end of class definition
class aaSetupSensors(Thread):
    
#     r = SensorListenerDetectHectors()
#     r.setup()
    
    
       
    t = None
    sensorAddress = None
    
    def __init__(self, sensorAddress):
         print "aaSetupSensorsInside init(SetupSensors)"
         self.sensorAddress = sensorAddress
         print "aaSetupSensors inside init(SetupSensors)", sensorAddress
    
    def run(self):
        
        
        while self.sensorAddress==None:
           self.currentThread().sleep(5000)
           print 'aaSetupSensors waiting for sensoraddress' 
        print "aaSetupSensors sensorAddress should not be none", self.sensorAddress
        self.t=CheckSensors(self.sensorAddress).start()

# 
#         print "Inside run(SetupSensors)"
#         while self.t == None:
#             print "Waiting t is None"
#             self.currentThread().sleep(1000)
#         print "t is not None"
#         while not self.currentThread().isInterrupted():
#             print "in thread"
#             self.t.waitForSensor()
# #             
# #             if self.sensor == "fwd1":
# #                 self.t.waitForForwardSensor1()
# #             elif self.sensor == "fwd2":
# #                 self.t.waitForForwardSensor2()
# #             elif self.sensor == "rev1":
# #                 self.t.waitForReverseSensor1()
# #             elif self.sensor == "rev2":
# #                 self.t.waitForReverseSensor2()
#             print ("Sensor detected")
#             try: 
#                 a=1
# #                 self.currentThread().sleep(5000)
#             except InterruptedException: 
#                 self.interrupt()         


#foo.start()

# l = MyLocoListener()
global enginespeed 
global jlo
enginespeed = 0.2
#connect each sensor to its loco and function, repeat as needed
# r=Automaton()
# s=r.setup("MS-N257E3", 3, "F1")  #horn for address 909

q=ThrottleFunctionForSensorListener()
q.setup(3)
# r.setupThrottleFunction(q)


Main.lo.addPropertyChangeListener(q)
Main.runFSM()

jlo=listenerObjects()
r = SensorListenerDetectHectors()
# r.setup()
jlo.addPropertyChangeListener(r)


rev1 = aaSetupSensors("MS-N257E1")
rev1.run()
fwd1 = aaSetupSensors("MS+N257E1")
fwd1.run()
rev2 = aaSetupSensors("MS-N257E3")
rev2.run()
fwd2 = aaSetupSensors("MS+N257E3")
fwd2.run()
# fwd1.run()
# rev1 = aaSetupSensors("rev1")
# rev1.run()
# fwd2 = aaSetupSensors("fwd2")
# fwd2.run()
# rev2 = aaSetupSensors("rev2")
# rev2.run()

