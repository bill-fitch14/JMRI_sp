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
class MyListener(java.beans.PropertyChangeListener):


    throttle1 = None  #make a spot to remember the throttle object that was passed in 
#   t1 = None
#   def setup(self, sensor, throttle, fnKey) :
    def setup(self, throttle) :
        self.throttle1 = throttle  #store for use later
        return
#      self.throttle = self.getThrottle(3, False)
#     self.fnKey = fnKey  #store for use later
#     sensors.provideSensor(sensor).addPropertyChangeListener(self)
    
 
# def __init__(self,throttle):
#   print "in _init"
#   self.t1 = throttle
#   if t1 is None:
#       print "in _init_t1 is null"
#   else:
#       print "in _init_t1 id not null"
#        
#                
#  
#   return
        

    def propertyChange(self, event):
      
        print "entered property change"
        print "propertyName" + event.propertyName
        print "oldVal", event.oldValue, "newVal", event.newValue
    
    #     if t1 is None:
    #         print "t1 is null"
    #     else:
    #         print "t1 is not null"
    #     if isinstance(new_year, Automaton) == true:
    #         print "ti instance of Automaton"  
        
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
        elif event.propertyName == "loco1F" and event.newValue >0 and event.newValue <=1 : 
        # get loco address. For long address change "False" to "True" 
#         self.throttle = self.getThrottle(3, False)  # short address 14
                    # set loco to forward
#         t1 = Automaton()
            if self.throttle1 is None:
                print "t1 is null"
            else:
                print "t1 id not null"
    #         t1.start()
            self.throttle1.setSpeedSetting(0)
            print "t1 id not null" 
            self.waitMsec(3000) 
            print " 3Set Speed finished"
    #         print " 4move Loco Forward" + "   Forwardreverse:" + str(True) + "   ThrottleValue" +str(ThrottleValue)
            self.throttle1.setIsForward(True) 
    #         self.throttle.moveLoco(True,0.1)
    #         Automaton().start()
            
    #         t.start()
    #         print "Set Loco Forward"
    #         self.throttle.setIsForward(True)
    #         
    #         # wait 1 second for engine to be stopped, then set speed
    #         self.waitMsec(1000)                 
    #         print "Set Speed"
            throttle1.setSpeedSetting(0.1) 

        
        elif event.propertyName == "loco1R" and event.newValue >=0 and event.newValue <=1 : 
        # get loco address. For long address change "False" to "True" 
#         self.throttle = self.getThrottle(3, False)  # short address 14
        # set loco to forward
            t1 = Automaton()
            if t1 is None:
                print "t1 is null"
            else:
                print "t1 id not null"
            t1.start()         
            t1.moveLoco(False,0.1)
    #         Automaton().start()
    #         Automaton().start()
    #         print "Set Speed1: " + event.propertyName
    #         print "Set Loco Reverse"
    #         self.throttle.setIsForward(False)
    #         
    #         # wait 1 second for engine to be stopped, then set speed
    #         self.waitMsec(1000)                 
    #         print "Set Speed"
            throttle1.setSpeedSetting(0.1)
            
        elif event.propertyName == "loco1SetSpeed" and event.newValue >=0 and event.newValue <=1 :
            
            print 'event.propertyName == "loco1SetSpeed"' 
    #         t1 = Automaton()
    #         if t1 is None:
    #             print "t1 is null"
    #         else:
    #             print "t1 id not null"
    #         t1.start()
    #         t1.setSpeed(0.1)
            print 'event.propertyName == "loco1SetSpeed"2' 
            
    #     elif event.propertyName == "loco1SetDirection" and event.newValue >=0 and event.newValue <=1 :
    #         
    #         print 'event.propertyName == "loco1SetDirection"' 
    #         t1 = Automaton()
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
    #         t1 = Automaton()
    #         if t1 is None:
    #             print "t1 is null"
    #         else:
    #             print "t1 id not null"
    #         t1.start()
    # #         milli = event.newValue
    #         t1.pause(1000)
    # 
    #         print 'event.propertyName == "pause" ' 
    #         Automaton().start()
    #         Automaton().start()
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

    global throttle1
    
    def init(self):
        print "Inside init(self)"

        # set up sensor numbers
        # fwdSensor is reached when loco is running forward
        self.fwdSensor = sensors.provideSensor("MS-N257E3")
        self.revSensor = sensors.provideSensor("MS-N257E1")

        # get loco address. For long address change "False" to "True" 
        self.throttle = self.getThrottle(3, False)  # short address 3
        
        
        if self.throttle == None:
            print 'throttle is none'
        else:
            print 'throttle is none'
                    
        throttle1 = self.throttle
                          
        self.throttle.setSpeedSetting(0) 
        self.waitMsec(1000) 


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
        
    
     
    def setSpeed(self,ThrottleValue):

        # wait 1 second for engine to be stopped, then set speed
        self.waitMsec(1000)                 
        print "Set Speed"
        print " 1Set Speed" + "   ThrottleValue" +str(ThrottleValue)
        self.throttle.setSpeedSetting(ThrottleValue) 
        print "Set Speed finished"
        
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
  
   

# end of class definition

# start one of these up
# Automaton().start()
# logger = Logger.getLogger("jmri.jmrit.jython.exec.Automaton")
print "start of script aa"
#connect each sensor to its loco and function, repeat as needed
# Automaton().start() 
# print "should have run setup(3)"
p1 = Main.lo
m = MyListener()
Automaton.setup(p)
# l = MyLocoListener()
p1.addPropertyChangeListener(m)
# p1.addPropertyChangeListener(l)
Main.runFSM()
