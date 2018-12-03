/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.electric.tool.dcs.SpecificStructures;

/**
 *
 * @author diivanov
 */
public final class ImmutableUnorderedPairOfStrings {
    private final String first;
    private final String second;

    /**
     *
     * @param first
     * @param second
     */
    public ImmutableUnorderedPairOfStrings(String first, String second) {
        if ((first == null) || (second == null)) {
            throw new NullPointerException("Transfer null object to pair");
        }
        this.first = first;
        this.second = second;
    }

    /**
     * Get First Object with generic type.
     *
     * @return
     */
    public String getFirstObject() {
        return first;
    }

    /**
     * Get Second Object with generic type.
     *
     * @return
     */
    public String getSecondObject() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ImmutableUnorderedPairOfStrings)) {
            return false;
        }
        ImmutableUnorderedPairOfStrings pair = (ImmutableUnorderedPairOfStrings) o;
        
        boolean firstCondition = false;
        if(pair.getFirstObject().equals(this.getFirstObject())) {
            firstCondition = true;
        }
        if(pair.getFirstObject().equals(this.getSecondObject())) {
            firstCondition = true;
        }
        
        boolean secondCondition = false;
        if(pair.getSecondObject().equals(this.getFirstObject())) {
            secondCondition = true;
        }
        if(pair.getSecondObject().equals(this.getSecondObject())) {
            secondCondition = true;
        }
        
        return firstCondition && secondCondition;
    }

    @Override
    public int hashCode() {
        return this.getFirstObject().hashCode() / 2 + this.getSecondObject().hashCode() / 2;
    }
    
    @Override
    public String toString() {
        String answer = "first string: " + getFirstObject().toString()
                + ", second string: " + getSecondObject().toString();
        return answer;
    }
}
