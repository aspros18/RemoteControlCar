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
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/BatteryPartialControllerData.o \
	${OBJECTDIR}/BridgeHandler.o \
	${OBJECTDIR}/CertificateException.o \
	${OBJECTDIR}/Config.o \
	${OBJECTDIR}/ControllerData.o \
	${OBJECTDIR}/ControllerSideMessageProcess.o \
	${OBJECTDIR}/ControllerSideVideoProcess.o \
	${OBJECTDIR}/DisconnectProcess.o \
	${OBJECTDIR}/FileUtils.o \
	${OBJECTDIR}/HostSideMessageProcess.o \
	${OBJECTDIR}/HostSideVideoProcess.o \
	${OBJECTDIR}/JpegListener.o \
	${OBJECTDIR}/JpegStore.o \
	${OBJECTDIR}/JpegStreamer.o \
	${OBJECTDIR}/Message.o \
	${OBJECTDIR}/MessageProcess.o \
	${OBJECTDIR}/SSLHandler.o \
	${OBJECTDIR}/SSLProcess.o \
	${OBJECTDIR}/SSLServerSocket.o \
	${OBJECTDIR}/SSLSocket.o \
	${OBJECTDIR}/SSLSocketException.o \
	${OBJECTDIR}/SSLSocketter.o \
	${OBJECTDIR}/ServerSocket.o \
	${OBJECTDIR}/Socket.o \
	${OBJECTDIR}/SocketBuffer.o \
	${OBJECTDIR}/SocketException.o \
	${OBJECTDIR}/SocketJpegListener.o \
	${OBJECTDIR}/StringUtils.o \
	${OBJECTDIR}/TestDisconnectProcess.o \
	${OBJECTDIR}/Timer.o \
	${OBJECTDIR}/main.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-Wall -std=c++0x -lssl -lcrypto -lpthread
CXXFLAGS=-Wall -std=c++0x -lssl -lcrypto -lpthread

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rcserver

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rcserver: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/rcserver ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/BatteryPartialControllerData.o: BatteryPartialControllerData.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/BatteryPartialControllerData.o BatteryPartialControllerData.cpp

${OBJECTDIR}/BridgeHandler.o: BridgeHandler.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/BridgeHandler.o BridgeHandler.cpp

${OBJECTDIR}/CertificateException.o: CertificateException.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/CertificateException.o CertificateException.cpp

${OBJECTDIR}/Config.o: Config.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/Config.o Config.cpp

${OBJECTDIR}/ControllerData.o: ControllerData.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/ControllerData.o ControllerData.cpp

${OBJECTDIR}/ControllerSideMessageProcess.o: ControllerSideMessageProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/ControllerSideMessageProcess.o ControllerSideMessageProcess.cpp

${OBJECTDIR}/ControllerSideVideoProcess.o: ControllerSideVideoProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/ControllerSideVideoProcess.o ControllerSideVideoProcess.cpp

${OBJECTDIR}/DisconnectProcess.o: DisconnectProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/DisconnectProcess.o DisconnectProcess.cpp

${OBJECTDIR}/FileUtils.o: FileUtils.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/FileUtils.o FileUtils.cpp

${OBJECTDIR}/HostSideMessageProcess.o: HostSideMessageProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/HostSideMessageProcess.o HostSideMessageProcess.cpp

${OBJECTDIR}/HostSideVideoProcess.o: HostSideVideoProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/HostSideVideoProcess.o HostSideVideoProcess.cpp

${OBJECTDIR}/JpegListener.o: JpegListener.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/JpegListener.o JpegListener.cpp

${OBJECTDIR}/JpegStore.o: JpegStore.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/JpegStore.o JpegStore.cpp

${OBJECTDIR}/JpegStreamer.o: JpegStreamer.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/JpegStreamer.o JpegStreamer.cpp

${OBJECTDIR}/Message.o: Message.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/Message.o Message.cpp

${OBJECTDIR}/MessageProcess.o: MessageProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/MessageProcess.o MessageProcess.cpp

${OBJECTDIR}/SSLHandler.o: SSLHandler.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLHandler.o SSLHandler.cpp

${OBJECTDIR}/SSLProcess.o: SSLProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLProcess.o SSLProcess.cpp

${OBJECTDIR}/SSLServerSocket.o: SSLServerSocket.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLServerSocket.o SSLServerSocket.cpp

${OBJECTDIR}/SSLSocket.o: SSLSocket.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLSocket.o SSLSocket.cpp

${OBJECTDIR}/SSLSocketException.o: SSLSocketException.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLSocketException.o SSLSocketException.cpp

${OBJECTDIR}/SSLSocketter.o: SSLSocketter.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SSLSocketter.o SSLSocketter.cpp

${OBJECTDIR}/ServerSocket.o: ServerSocket.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/ServerSocket.o ServerSocket.cpp

${OBJECTDIR}/Socket.o: Socket.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/Socket.o Socket.cpp

${OBJECTDIR}/SocketBuffer.o: SocketBuffer.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SocketBuffer.o SocketBuffer.cpp

${OBJECTDIR}/SocketException.o: SocketException.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SocketException.o SocketException.cpp

${OBJECTDIR}/SocketJpegListener.o: SocketJpegListener.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/SocketJpegListener.o SocketJpegListener.cpp

${OBJECTDIR}/StringUtils.o: StringUtils.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/StringUtils.o StringUtils.cpp

${OBJECTDIR}/TestDisconnectProcess.o: TestDisconnectProcess.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/TestDisconnectProcess.o TestDisconnectProcess.cpp

${OBJECTDIR}/Timer.o: Timer.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/Timer.o Timer.cpp

${OBJECTDIR}/main.o: main.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.cc) -g -MMD -MP -MF $@.d -o ${OBJECTDIR}/main.o main.cpp

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
