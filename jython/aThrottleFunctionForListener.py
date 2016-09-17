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
  throttle = None  #make a spot to remember the throttle object that was passed in
#   fnKey = None
  def setup(self, sensor,  throttle, fnKey) :
    if (sensor == None) : 
        print 'sensor is None'
        return    
    if (throttle == None) : 
        print 'throttle is None'
        return  
    self.throttle = throttle  #store for use later
    return
#     self.fnKey = fnKey  #store for use later
    
    listenerObj.addPropertyChangeListener(self)
    return
  def propertyChange(self, event):
    if (event.newValue == ACTIVE) :
        print "entered property change"
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
            print "Set Loco Reverse"
            self.throttle.setIsForward(False)
            self.waitMsec(1000)                 # wait 1 second for Xpressnet to catch up
            print "Set Speed"
            self.throttle.setSpeedSetting(0.1)
            

class Automaton(jmri.jmrit.automat.AbstractAutomaton) :
        #perform actions that need to be in a thread, such as loco acquisition 
        def init(self):
#             logger.debug("Inside Automaton.init("+self.sensorName+","+str(self.throttleAddress)+","+self.fnKeyName+")")
            print 'throttle address' , self.throttleAddress
            self.throttle = self.getThrottle(3, False)
            # actually attach the sensor to the loco
            ThrottleFunctionForSensorListener().setup(self.sensorName, self.throttle, self.fnKeyName)
            return    

        #pass and store needed values for this instance, then start the thread
        def setup(self, sensorName, throttleAddress, fnKeyName):
            self.sensorName = sensorName
            self.throttleAddress = throttleAddress           
            self.fnKeyName = fnKeyName           
            self.start()
            self.waitMsec(500)  #give it a chance to happen

####################################################################
logger = Logger.getLogger("jmri.jmrit.jython.exec.ThrottleFunctionForSensor")

#connect each sensor to its loco and function, repeat as needed
Automaton().setup("MS-N257E3", 3, "F1")  #horn for address 909
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

