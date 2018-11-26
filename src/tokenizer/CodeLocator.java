package tokenizer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class CodeLocator {

	final String fileContent;

	@Getter
	@AllArgsConstructor
	public class CodeLocation {

		public final int start;
		public final int end;

		public String getCode() {
			return fileContent.substring(start, end);
		}

		public CodeLocation merge(CodeLocation location) {
			return new CodeLocation(Math.min(start, location.start), Math.max(end, location.end));
		}
	}

	public CodeBranch branch() {
		return new CodeBranch();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public class CodeBranch {
		CodeBranch parent;
		int cursor;

		public int getStart() {
			return parent == null ? 0 : parent.getEnd();
		}

		public int getEnd() {
			return getStart() + cursor;
		}

		/**
		 * Returns the code encapsulated by this branch (up until the cursor)
		 * 
		 * @return the code inside this branch span
		 */
		public String getContent() {
			return fileContent.substring(getStart(), getEnd());
		}

		public CodeLocation getLocation() {
			return from(getStart()).to(getEnd());
		}

		/**
		 * Returns all the remaining code after the cursor
		 * 
		 * @return all code after the cursor
		 */
		public String getRest() {
			return fileContent.substring(getEnd());
		}

		public CodeBranch fork() {
			return new CodeBranch(this, 0);
		}

		public void advance(int nbCar) {
			cursor += nbCar;
		}

		public CodeLocation commit() {
			CodeLocation loc = getLocation();
			if (parent != null) {
				parent.advance(cursor);
			}
			return loc;
		}

		@Override
		public String toString() {
			return "<<<" + getContent() + ">>>" + getRest();
		}

	}

	public CodeLocationBuilder from(int from) {
		return new CodeLocationBuilder(from);
	}

	@AllArgsConstructor
	public class CodeLocationBuilder {

		final int from;

		public CodeLocation to(int end) {
			return new CodeLocation(from, end);
		}

		public CodeLocation length(int nbCar) {
			return new CodeLocation(from, from + nbCar);
		}
	}
}
