package org.oreon.gl.components.terrain;

import java.util.HashMap;

import org.oreon.core.gl.buffer.GLPatchVBO;
import org.oreon.core.gl.context.GLContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.wrapper.parameter.Default;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.util.MeshGenerator;

import lombok.Getter;

public class GLTerrain extends Node{
	
	@Getter
	private TerrainQuadtree quadtree;
		
	public GLTerrain(GLShaderProgram shader, GLShaderProgram wireframe, GLShaderProgram shadow)
	{
		GLContext.registerObject(new TerrainConfiguration());
		
		GLPatchVBO buffer  = new GLPatchVBO();
		buffer.addData(MeshGenerator.TerrainChunkMesh(),16);
		
		GLRenderInfo renderInfo = new GLRenderInfo(shader,
				   new Default(),
				   buffer);

		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(wireframe,
						    new Default(),
						    buffer);
		HashMap<NodeComponentType, NodeComponent> components = new HashMap<NodeComponentType, NodeComponent>();
		
		TerrainConfiguration config = new TerrainConfiguration();
		
		components.put(NodeComponentType.MAIN_RENDERINFO, renderInfo);
		components.put(NodeComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
		components.put(NodeComponentType.CONFIGURATION, config);
		
		quadtree = new TerrainQuadtree(components);
		
		addChild(quadtree);
		
		quadtree.start();
	}

}
