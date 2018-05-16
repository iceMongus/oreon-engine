package org.oreon.vk.components.water;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.FrameBufferDepthAttachment;
import org.oreon.core.vk.framebuffer.FrameBufferObject;
import org.oreon.core.vk.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.pipeline.RenderPass;

import lombok.Getter;

@Getter
public class ReflectionFbo extends FrameBufferObject{
	
	private FrameBufferColorAttachment albedoBuffer;
	private FrameBufferColorAttachment normalBuffer;
	private FrameBufferDepthAttachment depthBuffer;

	public ReflectionFbo(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties) {
		
		width = EngineContext.getConfig().getX_ScreenResolution()/2;
		height = EngineContext.getConfig().getY_ScreenResolution()/2;
		
		albedoBuffer = new FrameBufferColorAttachment(device, memoryProperties, width, height,
				VK_FORMAT_R8G8B8A8_UNORM);
		normalBuffer = new FrameBufferColorAttachment(device, memoryProperties, width, height, 
				VK_FORMAT_R16G16B16A16_SFLOAT);
		depthBuffer = new FrameBufferDepthAttachment(device, memoryProperties, width, height);
		
		renderPass = new RenderPass(device);
		renderPass.setAttachment(VK_FORMAT_R8G8B8A8_UNORM,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
				VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
				VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_D32_SFLOAT,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
				VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.addColorAttachmentReference(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addColorAttachmentReference(1, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addDepthAttachmentReference(2, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
		renderPass.setSubpassDependency(VK_SUBPASS_EXTERNAL, 0,
				VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
	    		VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
	    		VK_ACCESS_MEMORY_READ_BIT,
	    		VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
	    		VK_DEPENDENCY_BY_REGION_BIT);
		renderPass.setSubpassDependency(0, VK_SUBPASS_EXTERNAL,
				VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
				VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
	    		VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
	    		VK_ACCESS_MEMORY_READ_BIT,
	    		VK_DEPENDENCY_BY_REGION_BIT);
		renderPass.createSubpass();
		renderPass.createRenderPass();
		
		attachmentCount = 3;
		depthAttachment = true;
		
		frameBuffer = new VkFrameBuffer(device, width, height, 1,
				getpImageViews(), renderPass.getHandle());
	}
	
	public LongBuffer getpImageViews(){
		
		LongBuffer pImageViews = memAllocLong(3);
		pImageViews.put(0, albedoBuffer.getImageView().getHandle());
		pImageViews.put(1, normalBuffer.getImageView().getHandle());
		pImageViews.put(2, depthBuffer.getImageView().getHandle());
		
		return pImageViews;
	}
}
