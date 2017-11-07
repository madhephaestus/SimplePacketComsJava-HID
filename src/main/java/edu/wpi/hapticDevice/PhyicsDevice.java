package edu.wpi.hapticDevice;

import java.util.ArrayList;

import com.neuronrobotics.sdk.addons.kinematics.DHChain;
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics;
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR;
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;
import com.neuronrobotics.sdk.common.NonBowlerDevice;
import Jama.Matrix;
import groovy.lang.Closure;

public class PhyicsDevice extends NonBowlerDevice{
	HIDSimpleComsDevice hidEventEngine;
	DHParameterKinematics physicsSource ;
	int count = 0;
	Runnable event = ()-> {
	
	
			count ++;
			if(count >100){
				count =0;
						//Get the DHChain object
				DHChain chain = physicsSource.getChain();
				
				// Setup of variables done, next perfoem one compute cycle
				
				//get the current FK pose to update the data used by the jacobian computation
				TransformNR pose = physicsSource.getCurrentTaskSpaceTransform();
				// Convert the tip transform to Matrix form for math
				Matrix matrixForm= pose.getMatrixTransform();
				// get the position of all the joints in engineering units
				double[] jointSpaceVector = physicsSource.getCurrentJointSpaceVector();
				// compute the Jacobian using Jama matrix library
				Matrix jacobian =  chain.getJacobian(jointSpaceVector);
				// convert to the 3x6 marray of doubles for display
				double [][] data = jacobian.getArray();
	
			    //ArrayList<TransformNR> intChainLocal = chain.intChain;
				ArrayList<TransformNR> stateChainLocal = chain.getCachedChain();
			    double[] PreviousOmega = 0;
			    double [] corilousTerm = {0,0,0};
			    TransformNR previousTransform = new TransformNR();
				for (int i=0;i<jointSpaceVector.length;i++)
				{
					
					TransformNR previousTransformTranspose = new TransformNR(previousTransform.getMatrixTransform().transpose() );
					
					RotationNR  perviousRot = previousTransform.getRotation();
					double [][] rotationMatrix = perviousRot.getRotationMatrix() ;
					
					TransformNR rotationBetweenZeroAndI = stateChainLocal.get(i);
					TransformNR rotationBetweenZeroAndITranspose = new TransformNR(0,0,0,new TransformNR(rotationBetweenZeroAndI .getMatrixTransform().transpose()).getRotation() );
					
					
					double [][] zVector = {rotationMatrix[2]};		
					Matrix zMatrix = new Matrix(zVector);
					Matrix bRotationToAllignFrames = rotationBetweenZeroAndITranspose.getMatrixTransform().times(zMatrix);
					
					double [][] perviousTerm = {{0,0,0}};
					if(i>0){
						perviousTerm[i-1]=PreviousOmega;
					}
					Matrix angularVelocityOfLink = rotationBetweenZeroAndITranspose.times(new Matrix(perviousTerm))
												.add(bRotationToAllignFrames);
					
					corilousTerm[i]=angularVelocityOfLink.get(0,i);
					PreviousOmega= angularVelocityOfLink.get(0,i);
					previousTransform = rotationBetweenZeroAndI;
				}
				//println corilousTerm
			}
		}
	
	
	public PhyicsDevice(HIDSimpleComsDevice c,DHParameterKinematics  d){
		hidEventEngine=c;
		physicsSource=d;
		hidEventEngine.addEvent(37,event);
		
	}
	@Override
	public  void disconnectDeviceImp(){		
		//println "Physics Termination signel shutdown"
		hidEventEngine.removeEvent(37,event);
	}
	
	@Override
	public  boolean connectDeviceImp(){
		//println "Physics Startup signel "
	}
	public  ArrayList<String>  getNamespacesImp(){
		// no namespaces on dummy
		return new ArrayList<>();
	}
	
}