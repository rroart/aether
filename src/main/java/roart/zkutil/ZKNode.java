package roart.zkutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;

public class ZKNode implements Comparable<ZKNode> {
	private final String name;
	private String prefix;
	private int sequence = -1;
	private final Logger log = LoggerFactory.getLogger(ZKNode.class);

	public ZKNode(String name) {
		if (name == null) {
			throw new NullPointerException("name null");
		}
		this.name = name;
		this.prefix = name;
		int idx = name.lastIndexOf(Constants.ZKDELIMITER);
		if (idx >= 0) {
			this.prefix = name.substring(0, idx);
			String number = null;
			try {
				number = name.substring(idx + 1);
				this.sequence = Integer.parseInt(number);
			} catch (NumberFormatException e) {
				log.info("Number format " + number);
				log.error(Constants.EXCEPTION, e);
			} catch (ArrayIndexOutOfBoundsException e) {
				log.info("Array bound " + idx);
				log.error(Constants.EXCEPTION, e);
			}
    }
}

@Override
   	public String toString() {
    	return name.toString();
	}

@Override
    public boolean equals(Object object) {
    	if (this == object) {
    		return true;
    	}
    	if (object == null || getClass() != object.getClass()) {
    		return false;
    	}

    	ZKNode sequence = (ZKNode) object;

    	if (!name.equals(sequence.name)) {
    		return false;
    	}

    	return true;
	}

@Override
    public int hashCode() {
    	return name.hashCode();
	}

public int compareTo(ZKNode name) {
    int answer = this.prefix.compareTo(name.prefix);
    if (answer == 0) {
    	int s1 = this.sequence;
    	int s2 = name.sequence;
    	if (s1 == -1 && s2 == -1) {
    		return this.name.compareTo(name.name);
    	}
    	if (s1 == -1) {
    		answer = 1;
    	} else if (s2 == -1) {
    		answer = -1;
    	} else {
    		answer = s1 - s2;
    	}
    }
    return answer;
}

public String getName() {
    return name;
}

}