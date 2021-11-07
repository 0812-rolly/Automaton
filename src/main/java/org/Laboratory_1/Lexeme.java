package org.Laboratory_1;

import java.util.Objects;

public class Lexeme {
    private ClassOfLexeme classOfLexeme;
    private TypeOfLexeme typeOfLexeme;
    private String value;
    private int position;

    public Lexeme(ClassOfLexeme classOfLexeme, TypeOfLexeme typeOfLexeme, String value, int position){
        this.classOfLexeme = classOfLexeme;
        this.typeOfLexeme = typeOfLexeme;
        this.value = value;
        this.position = position;
    }

    public ClassOfLexeme getClassOfLexeme() {
        return classOfLexeme;
    }

    public void setClassOfLexeme(ClassOfLexeme classOfLexeme) {
        this.classOfLexeme = classOfLexeme;
    }

    public TypeOfLexeme getTypeOfLexeme() {
        return typeOfLexeme;
    }

    public void setTypeOfLexeme(TypeOfLexeme typeOfLexeme) {
        this.typeOfLexeme = typeOfLexeme;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Класс лексемы: " + classOfLexeme + "\nТип лексемы: " + typeOfLexeme + "\nЗначение: " + value + "\nПозиция: " + position + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(classOfLexeme, typeOfLexeme, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lexeme lexeme = (Lexeme) o;
        return Objects.equals(value, lexeme.value);
    }
}