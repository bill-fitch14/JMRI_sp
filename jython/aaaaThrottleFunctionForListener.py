# Map a sensor state to a specified Function on a specified loco address
#
# used at MWOT to let visitors control the horn and lights on locos, via fascia buttons 
#
# Author: Steve Todd, copyright 2014
# Part of the JMRI distribution
#
from sm2 import Main
import java
import jmri

from org.apache.log4j import Logger

# Define one sensor listener. 
class ThrottleFunctionForSensorListener(java.beans.PropertyChangeListener):
  automaton = None  #make a spot to remember the throttle object that was passed in
#   waitMsec = None
#   fnKey = None
#   def setup(self, sensor,  throttle, fnKey) :
#     print 'entering ThrottleFunctionForSensorListener setup'
#     if (sensor == None) : 
#         print 'sensor is None'
#         return
#     if (throttle == None) : 
#         print 'throttle is None'
#         return
# #     self.automaton = automaton
#     self.throttle = throttle  #store for use later
# #     self.fnKey = fnKey  #store for use later
#     self.throttle.setSpeedSetting(0)
#     self.throttle.setIsForward(True)
#     
#     self.throttle.setSpeedSetting(0.01)
    
#     self.waitMsec(500)                 # wait 1 second for Xpressnet to catch up

#     print 'about to set listener object'
# #     listenerObj.addPropertyChangeListener(self)
#     print 'set listener object'
#     return
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
        print "Set Loco Forward"
        if (self.automaton == None) : 
            print 'automation is None'
        self.automaton.moveLoco(True,0.01)
    elif event.propertyName == "loco1SetSpeed" and event.newValue >=0 and event.newValue <=1 :
        print 'SetSpeed'
        self.automaton.setSpeed(0.1)
    elif event.propertyName == "loco1R" and event.newValue >0 and event.newValue <=1 :
        print "Set loco Reverse"
        self.automaton.moveLoco(False,0.01)
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

    #pass and store needed values for this instance, then start the thread
#     def setup(self, sensorName, throttleAddress, fnKeyName):
#         self.sensorName = sensorName
#         self.throttleAddress = throttleAddress           
#         self.fnKeyName = fnKeyName           
#         self.start()
#         self.waitMsec(10000)  #give it a chance to happen
#         
#         return
        
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
        
        self.throttle.setSpeedSetting(0) 
        self.waitMsec(500) 
        print " 3Set Speed finished"
        print " 4move Loco Forward" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setIsForward(ForwardReverse)
        print " 5MoveLoco2"
#         # wait 1 second for engine to be stopped, then set speed
        self.waitMsec(500)                 
        print " 6move loco set speed" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(0.01) 
        print "moveloco finished"
        return 
    
    def setSpeed(self,ThrottleValue):

        # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(500)                 
        print "Set Speed"
        print " 1Set Speed" + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(0.01) 
        print "Set Speed finished"
        return  
#     def setupThrottleFunction(self, throttlefunction):
#         self.waitMsec(10000)
#         self.q=throttlefunction
#         if self.throttle==None:
#             print 'self.throttle2 is None'
#         self.q.setup(self.sensorName, self.throttle, self.fnKeyName)

####################################################################
logger = Logger.getLogger("jmri.jmrit.jython.exec.ThrottleFunctionForSensor")



# l = MyLocoListener()

#connect each sensor to its loco and function, repeat as needed
# r=Automaton()
# s=r.setup("MS-N257E3", 3, "F1")  #horn for address 909

q=ThrottleFunctionForSensorListener()
q.setup(3)
# r.setupThrottleFunction(q)


Main.lo.addPropertyChangeListener(q)
Main.runFSM()
# Automaton().setup("LS1004", 909, "F0")  #lights for same loco
# class Automaton(jmri.jmrit.automat.AbstractAutomaton) :
#         
#         #perform actions that need to be in a thread, such as loco acquisition 
#         def init(self):
# #             logger.debug("Inside Automaton.init("+self.sensorName+","+str(self.throttleAddress)+","+self.fnKeyName+")")
#             self.throttle = self.getThrottle(self.throttleAddress, False)
#             # actually attach the sensor to the loco
#             ThrottleFunctionForSensorListener().setup( self.throttle)
#             return    
# 
#         #pass and store needed values for this instance, then start the thread
#         def setup(self, sensorName, throttleAddress=3):
#             
#             self.sensorName = sensorName 
#             self.throttleAddress = throttleAddress           
# #             self.fnKeyName = fnKeyName           
#             self.start()
#             self.waitMsec(500)  #give it a chance to happen
# 
# ####################################################################
# logger = Logger.getLogger("jmri.jmrit.jython.exec.ThrottleFunctionForSensor")
# 
# global listenerObj
# listenerObj=Main.lo
# Automaton.setup("MS-N257E3",3)
# #connect each sensor to its loco and function, repeat as needed
# # Automaton().setup("LS1003", 909, "F1")  #horn for address 909
# # Automaton().setup("LS1004", 909, "F0")  #lights for same loco

