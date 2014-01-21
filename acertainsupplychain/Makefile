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
