package io.github.mufasa1976.calcmaster.records;

import io.github.mufasa1976.calcmaster.enums.Operator;

import java.util.List;
import java.util.Optional;

public record CalculationProperties(
    List<Operator> operators,
    AdditionProperties additionProperties,
    SubtractionProperties subtractionProperties,
    MultiplicationProperties multiplicationProperties,
    DivisionProperties divisionProperties,
    Optional<String> subheader,
    boolean toggleHide,
    int numberOfCalculations,
    boolean verticalDisplay) {
}