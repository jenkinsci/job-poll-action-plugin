/*
 * The MIT License
 * 
 * Copyright (c) 2012, Jesse Farinacci
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.jenkins.ci.plugins;

import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.triggers.SCMTrigger.SCMAction;
import hudson.util.HttpResponses;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.jelly.XMLOutput;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * A very simple {@link hudson.model.Action} which provides a <code>poll</code>
 * URL target to force polling for the specified
 * {@link hudson.model.AbstractProject}.
 * 
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class JobPollAction implements Action {
    @SuppressWarnings({ "rawtypes" })
    private final AbstractProject target;

    public JobPollAction(
            @SuppressWarnings("rawtypes") final AbstractProject target) {
        this.target = target;
    }

    public AbstractProject<?,?> getOwner() {
        return target;
    }

    
    public String getIconFileName() {
        return "clipboard.png";
    }

    public String getUrlName() {
        return "poll";
    }
    
    public String getDisplayName() {
    	return Messages.ActionLabel();
    }
    
    public String getTitle() {
    	String result = null;
    	SCMAction scmAction = target.getAction(SCMAction.class);
    	if(scmAction != null) {
    		result = scmAction.getDisplayName();
    	}
    	return result;
    }
    
    public String getLogText() {
    	String result = null;
    	SCMAction scmAction = target.getAction(SCMAction.class);
    	if(scmAction != null) {
    		try {
				result = scmAction.getLog();
				
			} catch (IOException e) {
			}
    	}
    	
    	return result;
    }
    
    public HttpResponse doLog(StaplerRequest req, StaplerResponse rsp ) {
    	HttpResponse result = new HttpResponse() {
			public void generateResponse(StaplerRequest req, StaplerResponse rsp,
					Object node) throws IOException, ServletException {
				SCMAction scmAction = target.getAction(SCMAction.class);
		    	if(scmAction != null) {
		    		try {
						String log = scmAction.getLog();
						rsp.getWriter().write(log);
						
					} catch (IOException e) {
					}
		    	}
				
			}
		};
    	
    	return result;
    }
    
    public void writeLogTo(XMLOutput out) throws IOException {
    	SCMAction scmAction = target.getAction(SCMAction.class);
    	if(scmAction != null) {
    		scmAction.writeLogTo(out);
    	}
    }
}
