package com.challenge.got.service.api;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.got.exception.model.NotFoundException;

/**
 * An interface that defines the operations to be performed on a model, and it
 * should interacts with the equivalent dao of the model
 * 
 * @param <T>
 *            the model's data type
 * @param <E>
 *            the model Id's data type
 */
@Service
@Transactional
interface MainService<T, E extends Serializable> {

	/**
	 * This method updates a model, given the model model
	 * 
	 * @param model
	 *            the model that need to be updated
	 * @return the updated model if updated successfully
	 */
	default T createOrUpdate(@Valid T model) {
		if (model == null) throw new IllegalArgumentException("Failed to create or update model, model shouldnot be null!");
		return getRepository().save(model);
	}

	/**
	 * This method removes a model, based on the given model's id
	 * 
	 * @param modelId
	 *            a model's Id
	 * @param <E>
	 *            the model's Id data type
	 */
	default void deleteById(E modelId) {
		T model = getById(modelId);

		if (model == null) {
			throw new NotFoundException(String.format("Model with the following id %s doesn't exist", modelId));
		}
		getRepository().delete(model);
	}

	/**
	 * This method returns a model, based on the given model's id
	 * 
	 * @param modelId
	 *            a model's Id
	 * @param <E>
	 *            the model's Id data type
	 * @return a model if model exists, otherwise it will return <b>null</b>
	 */
	default T getById(E modelId) {
		if (modelId == null) throw new IllegalArgumentException("Unable to find model, model id should not be null!");
		return getRepository()	.findById(modelId)
								.orElse(null);
	}

	/**
	 * This method returns a list of all models
	 * 
	 * @return a list of models
	 */
	default List<T> getAll() {
		return getRepository().findAll();
	}

	/**
	 * This method returns an implementation of {@link JpaRepository}
	 * 
	 * @return an implementation of {@link JpaRepository}
	 */
	JpaRepository<T, E> getRepository();
}
