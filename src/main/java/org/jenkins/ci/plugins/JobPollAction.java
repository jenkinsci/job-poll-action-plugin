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
import hudson.model.Job;
import hudson.scm.SCM;
import hudson.model.AbstractProject;
import hudson.triggers.SCMTrigger;
import hudson.triggers.SCMTrigger.SCMAction;
import jenkins.triggers.SCMTriggerItem;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;

import org.apache.commons.jelly.XMLOutput;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * A very simple {@link hudson.model.Action} which provides a <code>poll</code> URL target to force polling for the specified
 * {@link hudson.model.AbstractProject}.
 *
 * @author <a href="mailto:jieryn@gmail.com">Jesse Farinacci</a>
 * @since 1.0
 */
public final class JobPollAction implements Action {
	@SuppressWarnings({"rawtypes"})
	private final Job target;

	public JobPollAction(@SuppressWarnings("rawtypes") final Job target) {
		this.target = target;
	}

	public Job<?, ?> getOwner() {
		return target;
	}

	public String getIconFileName() {
		return "search.png";
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

			}
			catch(IOException e) {}
		}

		return result;
	}

	public HttpResponse doLog(StaplerRequest req, StaplerResponse rsp) {
		HttpResponse result = new HttpResponse() {
			public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
				SCMAction scmAction = target.getAction(SCMAction.class);
				if(scmAction != null) {
					try {
						XMLOutput xmlOutput = XMLOutput.createXMLOutput(rsp.getWriter());
						scmAction.writeLogTo(xmlOutput);
					}
					catch(IOException e) {}
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

	@SuppressWarnings("unchecked")
	public boolean isPollingEnabled() {
		boolean enabled = false;

		if (target instanceof AbstractProject) {
			AbstractProject project = (AbstractProject) target;
			enabled = project.getScm().supportsPolling() && project.getTrigger(SCMTrigger.class) != null;
		} else {
			try {
				if (target instanceof SCMTriggerItem) {
					Collection<? extends SCM> scms = ((SCMTriggerItem) target).getSCMs();
					for (Iterator<? extends SCM> i = scms.iterator(); i.hasNext(); ) {
						SCM scm = i.next();
						if (scm.supportsPolling()) {
							enabled = true;
							break;
						}
					}
				}
			} catch (Exception e) {
			}
		}

		return enabled;
	}

	public String getUrl() {
		return target.getUrl();
	}
}
