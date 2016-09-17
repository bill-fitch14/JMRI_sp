'''
Created on 20 Feb 2016

@author: bill
'''


from sm2 import Main
import java
import jmri

t0 = None
t1 = None
t2 = None

def setPoint(propertyName,newValue):
    if propertyName.equals("property1") and newValue.equals(0): 
#             turnouts.provideTurnout("MT+10").setState(CLOSED)
            print "turnouts.provideTurnout(MT+10).setState(CLOSED)"        
    elif propertyName.equals("property1") and newValue.equals(1):
#             turnouts.provideTurnout("MT+10").setState(THROWN)
            print "turnouts.provideTurnout(MT+10).setState(THROWN)"
    elif propertyName.equals("property2") and newValue.equals(0):
#             turnouts.provideTurnout("MT+20").setState(CLOSED)
            print "turnouts.provideTurnout(MT+20).setState(CLOSED)"
    elif propertyName.equals("property2") and newValue.equals(1):
#             turnouts.provideTurnout("MT+20").setState(THROWN)
            print "turnouts.provideTurnout(MT+20).setState(THROWN)"
    else:   print "Point not found, propertyName: ",propertyName, " newValue", newValue
    self.waitMsec(5000)
    return


# First, define the listener.  This one just prints some
# information on the change, but more complicated code is
# of course possible.
class MyListener(java.beans.PropertyChangeListener):


  throttle = None  #make a spot to remember the throttle object that was passed in 
  t0 = None
  t1 = None
  t2 = None

  def __init__(self):

    t0=Automaton()
    t0.start()
    t= Automaton()
    t.start()
    t1=Automaton()
    t.start()
#   def setup(self, sensor, throttle, fnKey) :
  def setup(self, throttle) :
#     if (sensor == None) : return
    print "Inside MyListener setup(self)"
    self.throttle = throttle  #store for use later
#     self.throttle = self.getThrottle(3, False)
#     self.fnKey = fnKey  #store for use later
#     sensors.provideSensor(sensor).addPropertyChangeListener(self)
    return
    
  def propertyChange(self, event):
    print "change zzzzz",event.propertyName
    print "from zzzz", event.oldValue, "to  zzzzz", event.newValue
#     setPoint(event.propertyName,event.newValue)          # create one of these, and start it running
#     
#     def setPoint(self,propertyName,newValue):
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
#     else:   
#         print "Point not found " , event.propertyName, " ", event.newValue, " " ,event.oldValue
# 
# 
# class MyLocoListener(java.beans.PropertyChangeListener):
#   def propertyChange(self, event):
#     print "change ",event.propertyName
#     print "from ", event.oldValue, "to  ", event.newValue
    elif event.propertyName == "loco1F" and event.newValue >0 and event.newValue <=1 : 
        # get loco address. For long address change "False" to "True" 
#         self.throttle = self.getThrottle(3, False)  # short address 14
                    # set loco to forward
        
        t0.setLocoForward()(True,event.newValue)
#         print "Set Loco Forward"
#         self.throttle.setIsForward(True)
#         
#         # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(1000)                 
#         print "Set Speed"
#         self.throttle.setSpeedSetting(event.newValue) 
        
    elif event.propertyName == "loco1R" and event.newValue >=0 and event.newValue <=1 : 
        # get loco address. For long address change "False" to "True" 
#         self.throttle = self.getThrottle(3, False)  # short address 14
                    # set loco to forward
        
        t.setLocoForward(False,event.newValue)
#         print "Set Speed1: " + event.propertyName
#         print "Set Loco Reverse"
#         self.throttle.setIsForward(False)
#         
#         # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(1000)                 
#         print "Set Speed"
#         self.throttle.setSpeedSetting(event.newValue)
        
    elif event.propertyName == "loco1SetSpeed" and event.newValue >=0 and event.newValue <=1 : 
        
        Automaton().setup(3)
#         t1.setLocoForward(False,event.newValue)
#                     # set loco to forward
#         print "Set Loco Reverse2"
#         self.throttle.setIsForward(False)
#         print "Set Loco Reverse Done2" 
# #         wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(1000)                 
#         print "Set Speed1: " + event.propertyName
#         self.throttle.setSpeedSetting(event.newValue)   
        
    else:
        print "error!!!!"
        print "property name: " ,event.propertyName 
        print "event new val =" , event.newValue, " event old value" , event.oldValue
    return

class Automaton(jmri.jmrit.automat.AbstractAutomaton) :
        #perform actions that need to be in a thread, such as loco acquisition
    
    def init(self):
#         self.start()
#         self.waitMsec(5000)  #give it a chance to happen
#             logger.debug("Inside Automaton.init("+str(self.throttleAddress)+")")
#         print "Inside Automaton init(self)" + str(self.throttleAddress)
#         self.throttle = self.getThrottle(self.throttleAddress, False)
        self.throttle = self.getThrottle(3, False)  # short address 14
        print "Inside Automaton init(self) have got the throttle"
        self.waitMsec(5000)  
        # actually attach the sensor to the loco
#         MyListener().setup(self.throttle)
#         self.waitMsec(5000)  
        return    

    #pass and store needed values for this instance, then start the thread
    def setup(self,  throttleAddress):
        
        print "in Automaton Setup: " + str(throttleAddress)
#             self.sensorName = sensorName
        self.throttleAddress = throttleAddress           
#       
        print "throttle address: " + str(self.throttleAddress)          
        self.start()
        self.waitMsec(5000)  #give it a chance to happen
        self.throttle.setSpeedSetting(0.7) 
        
    def setLocoForward(self,ForwardReverse, ThrottleValue):
        self.start()
        print "Set Loco Forward"
        self.getThrottle(3, False).setIsForward(ForwardReverse)
        
        # wait 1 second for engine to be stopped, then set speed
        self.waitMsec(1000)                 
        print "Set Speed"
        self.throttle.setSpeedSetting(ThrottleValue) 

####################################################################
# logger = Logger.getLogger("jmri.jmrit.jython.exec.Automaton")
print "start of script"
#connect each sensor to its loco and function, repeat as needed
# Automaton().start() 
# print "should have run setup(3)"
p1 = Main.lo
m = MyListener()
# l = MyLocoListener()
p1.addPropertyChangeListener(m)
# p1.addPropertyChangeListener(l)
Main.runFSM()