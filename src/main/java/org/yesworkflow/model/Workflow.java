package org.yesworkflow.model;

import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.comments.BeginComment;

public class Workflow extends Program {

	public final List<Program> programs;
	public final List<Channel> channels;
	
	public Workflow(List<Program> programs, List<Channel> channels, BeginComment comment) {
		super(comment);
		this.programs = programs;
		this.channels = channels;
	}
	
	public static class Builder {
		
		public List<Program> programs = new LinkedList<Program>();
		public List<Channel> channels = new LinkedList<Channel>();
		public BeginComment comment;

		public Builder comment(BeginComment comment) {
			this.comment = comment;
			return this;
		}
		
		public Builder program(Program program) {
			this.programs.add(program);
			return this;
		}

		public Builder program(Channel channel) {
			this.channels.add(channel);
			return this;
		}

		public Program build() {
			
			if (programs.size() == 0) {
				
				return new Program(
						comment
				);

			} else {
				
				return new Workflow(
						programs,
						channels,
						comment
				);
			}
		}
	}
}

