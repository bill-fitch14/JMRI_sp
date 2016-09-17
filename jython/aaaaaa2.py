# This is an example script for a JMRI "Automat" in Python
# It is based on the AutomatonExample.
#
# It listens to two sensors, running a locomotive back and
# forth between them by changing its direction when a sensor
# detects the engine. 
#
# Author:  Howard Watkins, January 2007.
# Part of the JMRI distribution

from sm2 import Main
import java
import jmri
# import pydevd;pydevd.settrace()


# First, define the listener.  This one just prints some
# information on the change, but more complicated code is
# of course possible.
class MyListener(java.beans.PropertyChangeListener,jmri.jmrit.automat.AbstractAutomaton):


  throttle = None  #make a spot to remember the throttle object that was passed in 
#   t1 = None
# #   def setup(self, sensor, throttle, fnKey) :
#   def setup(self, throttle) :
# #     if (sensor == None) : return
#     print "Inside MyListener setup(self)"
#     self.throttle = throttle  #store for use later
# #     self.throttle = self.getThrottle(3, False)
# #     self.fnKey = fnKey  #store for use later
# #     sensors.provideSensor(sensor).addPropertyChangeListener(self)
#     return
 
  def __init__(self):
      print "in _init"
      
      self.throttle = self.getThrottle(3, False)
      self.waitMsec(3000)
      self.throttle.setSpeedSetting(0) 
      self.waitMsec(3000)
      self.throttle.setIsForward(False)
#       t1 = Test14()
#       if t1 is None:
#           print "in _init_t1 is null"
#       else:
#           print "in _init_t1 id not null"
#            
#                    
#       t1.start()
      return
        

  def propertyChange(self, event):
      
    print "entered property change"
    print "propertyName" + event.propertyName
    print "oldVal", event.oldValue, "newVal", event.newValue

#     if t1 is None:
#         print "t1 is null"
#     else:
#         print "t1 is not null"
#     if isinstance(new_year, Test14) == true:
#         print "ti instance of Test14"  
    
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
        
        print 'loco1F'
        # get loco address. For long address change "False" to "True" 
#         self.throttle = self.getThrottle(3, False)  # short address 14
                    # set loco to forward
#         t1 = Test14()
        if self.throttle is None:
            print "throttle is null"
        else:
            print "throttle id not null"
#         self.start() 
#         t1.
#         self.waitMsec(2000)                 
#         print "MoveLoco"
#         self.waitMsec(2000)                 
#         print " 1Set Speed"
# #         print " 2move Loco Forewared" + "   ThrottleValue" +str(ThrottleValue)
#         self.throttle.setSpeedSetting(0.1) 
#         self.waitMsec(3000) 
#         print " 3Set Speed finished"
# #         print " 4move Loco Forward" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
#         self.throttle.setIsForward(True)
#         print " 5MoveLoco2"
# #         # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(1000)                 
#         print " 6move loco set speed" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
#         self.throttle.setSpeedSetting(0.2) 
#         print "moveloco finished"
        moveLoco(True,0.1)
#         Test14().start()
        
#         self.start()
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
#         t1 = Test14()
#         if t1 is None:
#             print "t1 is null"
#         else:
#             print "t1 id not null"
#         t1.start()


#         self.waitMsec(2000)                 
#         print "MoveLoco"
#         self.waitMsec(2000)                 
#         print " 1Set Speed"
#         print " 2move Loco Forewared" + "   ThrottleValue" +str(ThrottleValue)
#         self.throttle.setSpeedSetting(0) 
#         self.waitMsec(3000) 
#         print " 3Set Speed finished"
#         print " 4move Loco Forward" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
#         self.throttle.setIsForward(False)
#         print " 5MoveLoco2"
# #         # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(1000)                 
#         print " 6move loco set speed" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
#         self.throttle.setSpeedSetting(0.2) 
#         print "moveloco finished"













         
        self.moveLoco(False,0.1)
#         Test14().start()
#         Test14().start()
#         print "Set Speed1: " + event.propertyName
#         print "Set Loco Reverse"
#         self.throttle.setIsForward(False)
#         
#         # wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(1000)                 
#         print "Set Speed"
#         self.throttle.setSpeedSetting(event.newValue)
        
    elif event.propertyName == "loco1SetSpeed" and event.newValue >=0 and event.newValue <=1 :
        
        print 'event.propertyName == "loco1SetSpeed"' 
#         t1 = Test14()
#         if t1 is None:
#             print "t1 is null"
#         else:
#             print "t1 id not null"
#         t1.start()
        setSpeed(0.1)
        print 'event.propertyName == "loco1SetSpeed"2' 
        
#     elif event.propertyName == "loco1SetDirection" and event.newValue >=0 and event.newValue <=1 :
#         
#         print 'event.propertyName == "loco1SetDirection"' 
#         t1 = Test14()
#         if t1 is None:
#             print "t1 is null"
#         else:
#             print "t1 id not null"
#         t1.start()
#         print "t1 started"
#         if event.newValue == 0:
#             print "setting direction false"
#             t1.setDirection(False)
#         else:
#             print "setting direction True"
#             t1.setDirection(True)
#         print 'event.propertyName == "loco1SetSpeed"2' 
#         
#         
#     elif event.propertyName == "pause" and event.newValue >=0 and event.newValue <=10000 :
#         
#         print 'event.propertyName == "pause"' 
#         t1 = Test14()
#         if t1 is None:
#             print "t1 is null"
#         else:
#             print "t1 id not null"
#         t1.start()
# #         milli = event.newValue
#         t1.pause(1000)
# 
#         print 'event.propertyName == "pause" ' 
#         Test14().start()
#         Test14().start()
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

# class Test14() :

   
    def init(self):
        # init() is called exactly once at the beginning to do
        # any necessary configuration.
        print "Inside init(self)"

        # set up sensor numbers
        # fwdSensor is reached when loco is running forward
        self.fwdSensor = sensors.provideSensor("MS-N257E3")
        self.revSensor = sensors.provideSensor("MS-N257E1")

        # get loco address. For long address change "False" to "True" 
        self.throttle = self.getThrottle(3, False)  # short address 3
                          
        self.throttle.setSpeedSetting(0) 
        self.waitMsec(1000) 
        print "init(self) finished"

        return

#     def handle(self):
# #         # handle() is called repeatedly until it returns false.
# #         print "Set Loco Forward"
# #         self.throttle.setIsForward(True)
# #          
# #         # wait 1 second for layout to catch up, then set speed
# #         self.waitMsec(1000)                 
# #         print "Set Speed"
# #         self.throttle.setSpeedSetting(0.7)
# #         print "wait 20 seconds"
# #         self.waitMsec(1000)
# #         self.throttle.setSpeedSetting(0.0)
#         return False
   
    def moveLoco(self,ForwardReverse, ThrottleValue):
        print "move loco"
#         self.start()
#         wait 1 second for engine to be stopped, then set speed
        self.waitMsec(2000)                 
        print "MoveLoco"
        self.waitMsec(2000)                 
        print " 1Set Speed"
        print " 2move Loco Forewared" + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(0) 
        self.waitMsec(3000) 
        print " 3Set Speed finished"
        print " 4move Loco Forward" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setIsForward(ForwardReverse)
        print " 5MoveLoco2"
#         # wait 1 second for engine to be stopped, then set speed
        self.waitMsec(1000)                 
        print " 6move loco set speed" + "   Forwardreverse:" + str(ForwardReverse) + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(0.2) 
        print "moveloco finished"
        return
    
     
    def setSpeed(self,ThrottleValue):

        # wait 1 second for engine to be stopped, then set speed
        self.waitMsec(1000)                 
        print "Set Speed"
        print " 1Set Speed" + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(ThrottleValue) 
        print "Set Speed finished"
        return
#     
#     def setDirection(self,ForwardReverse):
#         
# #         wait 1 second for engine to be stopped, then set speed
#         self.waitMsec(1000)                 
#         print "Set Direction"
#         print " 1Set Direction" + "   Direction" +str(ForwardReverse)
#         self.throttle.setIsForward(True) 
#         print "Set Speed finished"
#         return
    
#     def pause(self,milli):
#         
# #         wait mill second for engine to be stopped, then set speed
#         print "wait milli seconds"
#         self.waitMsec(milli)                 
#         
#         print "pause finished"
#         return
  
    def backAndForth(self):
        print "Inside handle(self)"

        # set loco to forward
        print "Set Loco Forward"
        self.throttle.setIsForward(True)
        
        # wait 1 second for layout to catch up, then set speed
        self.waitMsec(1000)                 
        print "Set Speed"
        self.throttle.setSpeedSetting(0.7)

        # wait for sensor in forward direction to trigger, then stop
        print "Wait for Forward Sensor"
        self.waitSensorActive(self.fwdSensor)
        print "Set Speed Stop"
        self.throttle.setSpeedSetting(0)
        
        # delay for a time (remember loco could still be moving
        # due to simulated or actual inertia). Time is in milliseconds
        print "wait 20 seconds"
        self.waitMsec(5000)          # wait for 20 seconds
        
        # turn on whistle, set direction to reverse, set speed
        self.throttle.setF3(True)     # turn on whistle
        self.waitMsec(1000)           # wait for 1 seconds
        self.throttle.setF3(False)    # turn off whistle
        self.waitMsec(1000)           # wait for 1 second

        print "Set Loco Reverse"
        self.throttle.setIsForward(False)
        self.waitMsec(1000)                 # wait 1 second for Xpressnet to catch up
        print "Set Speed"
        self.throttle.setSpeedSetting(0.1)

        # wait for sensor in reverse direction to trigger
        print "Wait for Reverse Sensor"
        self.waitSensorActive(self.revSensor)
        print "Set Speed Stop"
        self.throttle.setSpeedSetting(0)
        
        # delay for a time (remember loco could still be moving
        # due to simulated or actual inertia). Time is in milliseconds
        print "wait 20 seconds"
        self.waitMsec(5000)          # wait for 20 seconds
        
        # turn on whistle, set direction to forward, set speed
        self.throttle.setF3(True)     # turn on whistle
        self.waitMsec(1000)           # wait for 1 seconds
        self.throttle.setF3(False)    # turn off whistle
        self.waitMsec(1000)           # wait for 1 second
        self.throttle.setIsForward(True)
        self.waitMsec(1000)
        # and continue around again
        print "End of Loop"
        return 0   
        # (requires JMRI to be terminated to stop - caution
        # doing so could leave loco running if not careful)

# end of class definition

# start one of these up
# Test14().start()
# logger = Logger.getLogger("jmri.jmrit.jython.exec.Automaton")
print "start of script aa"
#connect each sensor to its loco and function, repeat as needed
# Automaton().start() 
# print "should have run setup(3)"
p1 = Main.lo
m = MyListener()
m.init()
# l = MyLocoListener()
p1.addPropertyChangeListener(m)
# p1.addPropertyChangeListener(l)
Main.runFSM()
