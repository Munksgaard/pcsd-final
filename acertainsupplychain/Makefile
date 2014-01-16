.PHONY: compile clean server jar

JAVAFILES=`find src -name '*.java'`

# repository root
LIBDIR=lib

compile:
	mkdir -p bin
	javac -source 1.6 -Xlint:all -d bin -cp $(LIBDIR)/*:src $(JAVAFILES)

clean:
	rm -rvf bin

CertainItemSupplier:
	java -ea -cp $(LIBDIR)/*:bin com.acertainsupplychain.business.CertainItemSupplier

CertainOrderManager:
	java -ea -cp $(LIBDIR)/*:bin com.acertainsupplychain.business.CertainOrderManager
