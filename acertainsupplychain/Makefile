.PHONY: compile clean server jar

JAVAFILES=`find src -name '*.java'`

# repository root
LIBDIR=lib

compile:
	mkdir -p bin
	javac -source 1.6 -Xlint:all -d bin -cp $(LIBDIR)/*:src $(JAVAFILES)

clean:
	rm -rvf bin
