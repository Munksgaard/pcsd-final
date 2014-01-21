.PHONY: compile clean server jar

JAVAFILES=`find src -name '*.java'`

# repository root
LIBDIR=lib

compile:
	mkdir -p bin
	javac -source 1.6 -Xlint:all -d bin -cp $(LIBDIR)/*:src $(JAVAFILES)

clean:
	rm -rvf bin

ItemSupplierTest:
	java -ea -cp $(LIBDIR)/*:bin com.acertainsupplychain.tests.ItemSupplierTest

OrderManagerTest:
	java -ea -cp $(LIBDIR)/*:bin com.acertainsupplychain.tests.OrderManagerTest

TestServer1:
	java -ea -cp $(LIBDIR)/*:bin \
	  com.acertainsupplychain.server.ItemSupplierHTTPServer 8080 \
	  0 0 1 2 3 4 5

TestServer2:
	java -ea -cp $(LIBDIR)/*:bin \
	  com.acertainsupplychain.server.OrderManagerHTTPServer 8000 0 \
	  "http://localhost:8080"

performance: compile
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.ItemServer &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 0 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 1 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 2 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 3 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 4 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 5 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 6 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 7 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 8 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 9 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 10 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 11 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 12 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 13 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 14 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 15 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 16 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 17 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 18 &
	java -cp $(LIBDIR)/*:bin com.acertainsupplychain.performance.OrderServer 19 &

