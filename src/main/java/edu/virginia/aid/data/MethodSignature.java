package edu.virginia.aid.data;

import org.apache.commons.lang3.StringUtils;

public class MethodSignature {

    private String name;
    private String[] params;

    public MethodSignature(String name, String[] params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MethodSignature) {
            MethodSignature other = (MethodSignature) o;
            if (name.equals(other.getName()) && params.length == other.getParams().length) {
                boolean paramsEqual = true;
                for (int i = 0; i < params.length; i++) {
                    if (!params[i].equals(other.getParams()[i])) {
                        paramsEqual = false;
                    }
                }

                if (paramsEqual) return true;
            }
        }

        return false;
    }

    public String toString() {
        return name + "(" + StringUtils.join(params, ", ") + ")";
    }
}
