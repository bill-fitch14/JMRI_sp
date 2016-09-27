import java
import jmri
import time
from java.lang import Thread, InterruptedException

class CheckSensors(jmri.jmrit.automat.AbstractAutomaton) :

        fwdSensor1 = None
        revSensor1 = None
        fwdSensor2 = None
        revSensor2 = None
        def init(self):
            # init() is called exactly once at the beginning to do
            # any necessary configuration.
            print "Inside init(CheckSensorRev)"
    
            # set up sensor numbers
            # fwdSensor is reached when loco is running forward
            self.revSensor2 = sensors.provideSensor("MS-N257E3")
            self.revSensor1 = sensors.provideSensor("MS-N257E1")
            self.fwdSensor2 = sensors.provideSensor("MS+N257E3")
            self.fwdSensor1 = sensors.provideSensor("MS+N257E1")
            
            print "end of init(CheckSensorRev)"
            
            return
    
        def handle(self):
            # handle() is called repeatedly until it returns false.
            print "Inside handle(CheckSensorRev)"
            return 0    
            # (requires JMRI to be terminated to stop - caution

            
        def waitForReverseSensor1(self):
            print "Yippo"
            
            while self.revSensor1 == None:
                if self.revSensor1 == None:
                    print "self.revSensor == None"
                    self.revSensor1 = sensors.provideSensor("MS-N257E1")
                    time.sleep(10)
                else:
                    print "self.revSensor != None"
            print "self.revSensor != None"
            self.waitSensorActive(self.revSensor1)
            print "Yippee"
            
        def waitForReverseSensor2(self):
            print "Yippo"
            
            while self.revSensor2 == None:
                if self.revSensor2 == None:
                    print "self.revSensor == None"
                    self.revSensor1 = sensors.provideSensor("MS-N257E3")
                    time.sleep(10)
                else:
                    print "self.revSensor != None"
            print "self.revSensor != None"
            self.waitSensorActive(self.revSensor2)
            print "Yippee"
            
        def waitForForwardSensor1(self):
            print "Yippo"
            
            while self.fwdSensor1 == None:
                if self.fwdSensor1 == None:
                    print "self.revSensor == None"
                    self.fwdSensor1 = sensors.provideSensor("MS+N257E1")
                    time.sleep(10)
                else:
                    print "self.revSensor != None"
            print "self.revSensor != None"
            self.waitSensorActive(self.fwdSensor1)
            t.addPropertyChangeListener(self.fwdSensor1)
            print "Yippee"
            
        def waitForForwardSensor2(self):
            print "Yippo"
            
            while self.fwdSensor2 == None:
                if self.fwdSensor2 == None:
                    print "self.revSensor == None"
                    self.fwdSensor2 = sensors.provideSensor("MS+N257E3")
                    time.sleep(10)
                else:
                    print "self.revSensor != None"
            print "self.revSensor != None"
            self.waitSensorActive(self.fwdSensor2)
            print "Yippee"
    
    # end of class definition
class aaSetupSensors(Thread):   
    t = None
    sensor = None
    
    def __init__(self, sensor):
         print "Inside init(SetupSensors)"
         self.sensor = sensor
    
    def run(self):
        self.t=CheckSensors()

        print "Inside run(SetupSensors)"
        while self.t == None:
            print "Waiting t is None"
            self.currentThread().sleep(5000)
        print "t is not None"
        while not self.currentThread().isInterrupted():
            print "in thread"
            if self.sensor == "fwd1":
                self.t.waitForForwardSensor1()
            elif self.sensor == "fwd2":
                self.t.waitForForwardSensor2()
            elif self.sensor == "rev1":
                self.t.waitForReverseSensor1()
            elif self.sensor == "rev2":
                self.t.waitForReverseSensor2()
            print ("revSensor reached")
            try: 
                self.currentThread().sleep(5000)
            except InterruptedException: 
                self.interrupt()
        
