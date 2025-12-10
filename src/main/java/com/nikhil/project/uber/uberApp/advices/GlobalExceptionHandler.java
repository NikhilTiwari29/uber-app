package com.nikhil.project.uber.uberApp.advices;

import com.nikhil.project.uber.uberApp.exceptions.DistanceCalculationException;
import com.nikhil.project.uber.uberApp.exceptions.UserAlreadyExistsException;
import com.nikhil.project.uber.uberApp.exceptions.RiderNotFoundException;
import com.nikhil.project.uber.uberApp.exceptions.RideRequestNotFoundException;
import com.nikhil.project.uber.uberApp.exceptions.RideRequestCannotBeAcceptedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Handle user already exists
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExists(UserAlreadyExistsException ex) {

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(ex.getMessage())
                .subErrors(List.of("Duplicate email detected"))
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Rider not found (e.g., when loading current rider)
    @ExceptionHandler(RiderNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleRiderNotFound(RiderNotFoundException ex) {

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .subErrors(List.of("Rider does not exist or is inactive"))
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Ride request not found
    @ExceptionHandler(RideRequestNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleRideRequestNotFound(RideRequestNotFoundException ex) {

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .subErrors(List.of("Ride request ID is invalid or no longer available"))
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Ride request cannot be accepted (wrong state)
    @ExceptionHandler(RideRequestCannotBeAcceptedException.class)
    public ResponseEntity<ApiResponse<?>> handleRideRequestCannotBeAccepted(
            RideRequestCannotBeAcceptedException ex) {

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST) // or HttpStatus.CONFLICT if you prefer
                .message(ex.getMessage())
                .subErrors(List.of("Ride request is not in PENDING state"))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Validation failed")
                .subErrors(errors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Handle constraint violations (@RequestParam, @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolations(ConstraintViolationException ex) {

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Constraint violation")
                .subErrors(errors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Handle malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidJson(HttpMessageNotReadableException ex) {

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Invalid request body / malformed JSON")
                .subErrors(List.of(ex.getMostSpecificCause().getMessage()))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Distance calculation issues (external service)
    @ExceptionHandler(DistanceCalculationException.class)
    public ResponseEntity<ApiResponse<?>> handleDistanceError(DistanceCalculationException ex) {

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message("Unable to calculate distance at the moment. Please try again later.")
                .subErrors(List.of(ex.getMessage()))
                .build();

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse<>(apiError));
    }

    // ✅ Fallback for all unhandled exceptions (keep this last)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {

        ApiError apiError = ApiError.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong. Please contact support.")
                .subErrors(List.of(ex.getMessage()))
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(apiError));
    }
}
