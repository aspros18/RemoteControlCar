#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux-x86
CND_DLIB_EXT=so
CND_CONF=Release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/Config.o \
	${OBJECTDIR}/FileUtils.o \
	${OBJECTDIR}/SSLHandler.o \
	${OBJECTDIR}/SSLProcess.o \
	${OBJECTDIR}/SSLSocketter.o \
	${OBJECTDIR}/StringUtils.o \
	${OBJECTDIR}/TestHandler.o \
	${OBJECTDIR}/TestProcess.o \
	${OBJECTDIR}/main.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-Wall -lpthread -lSSLSocket
CXXFLAGS=-Wall -lpthread -lSSLSocket

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-Llib

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rcserver

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rcserver: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rcserver ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/Config.o: Config.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/Config.o Config.cpp

${OBJECTDIR}/FileUtils.o: FileUtils.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/FileUtils.o FileUtils.cpp

${OBJECTDIR}/SSLHandler.o: SSLHandler.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLHandler.o SSLHandler.cpp

${OBJECTDIR}/SSLProcess.o: SSLProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLProcess.o SSLProcess.cpp

${OBJECTDIR}/SSLSocketter.o: SSLSocketter.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLSocketter.o SSLSocketter.cpp

${OBJECTDIR}/StringUtils.o: StringUtils.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/StringUtils.o StringUtils.cpp

${OBJECTDIR}/TestHandler.o: TestHandler.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/TestHandler.o TestHandler.cpp

${OBJECTDIR}/TestProcess.o: TestProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/TestProcess.o TestProcess.cpp

${OBJECTDIR}/main.o: main.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -O2 -Iinc -MMD -MP -MF $@.d -o ${OBJECTDIR}/main.o main.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rcserver

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
