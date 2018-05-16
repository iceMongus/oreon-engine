package org.oreon.gl.demo.oreonworlds.shaders.assets.plants;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.List;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.instanced.GLInstancedCluster;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.instanced.InstancedCluster;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;
import org.oreon.core.util.ResourceLoader;

public class TreeLeavesShader extends GLShaderProgram{
	
	private static TreeLeavesShader instance = null;

	public static TreeLeavesShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TreeLeavesShader();
	    }
	      return instance;
	}
	
	protected TreeLeavesShader()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Tree_Shader/TreeLeaves_VS.glsl"));
		addGeometryShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Tree_Shader/TreeLeaves_GS.glsl"));
		addFragmentShader(ResourceLoader.loadShader("oreonworlds/shaders/assets/Tree_Shader/TreeLeaves_FS.glsl"));
		compileShader();
		
		addUniform("material.diffusemap");
		addUniform("clipplane");
		addUniform("scalingMatrix");
		addUniform("isReflection");
		
		addUniformBlock("worldMatrices");
		addUniformBlock("modelMatrices");
		addUniformBlock("Camera");
//		addUniform("screenWidth");
//		addUniform("screenHeight");
		
		for (int i=0; i<100; i++)
		{
			addUniform("matrixIndices[" + i + "]");
		}
	}	
	
	public void updateUniforms(Renderable object)
	{
		bindUniformBlock("Camera", Constants.CameraUniformBlockBinding);
//		setUniformi("screenWidth", CoreSystem.getInstance().getWindow().getWidth());
//		setUniformi("screenHeight", CoreSystem.getInstance().getWindow().getHeight());
		setUniformi("isReflection", EngineContext.getRenderState().isReflection() ? 1 : 0);
		
		((GLInstancedCluster) object.getParentNode()).getWorldMatricesBuffer().bindBufferBase(0);
		bindUniformBlock("worldMatrices", 0);
		((GLInstancedCluster) object.getParentNode()).getModelMatricesBuffer().bindBufferBase(1);
		bindUniformBlock("modelMatrices", 1);
		
		setUniform("clipplane", EngineContext.getRenderState().getClipplane());
		setUniform("scalingMatrix", new Matrix4f().Scaling(object.getWorldTransform().getScaling()));
		
		@SuppressWarnings("unchecked")
		Material<GLTexture> material = (Material<GLTexture>) object.getComponent(NodeComponentType.MATERIAL0);

		glActiveTexture(GL_TEXTURE0);
		material.getDiffusemap().bind();
		setUniformi("material.diffusemap", 0);
		
		List<Integer> indices = ((InstancedCluster) object.getParentNode()).getHighPolyIndices();
					
		for (int i=0; i<indices.size(); i++)
		{
			setUniformi("matrixIndices[" + i +"]", indices.get(i));	
		}
	}
}
