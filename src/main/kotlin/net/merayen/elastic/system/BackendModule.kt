package net.merayen.elastic.system

/**
 * The backend module.
 * It should be initialized after all other modules as it should automatically restore project when instantiated.
 */
abstract class BackendModule(val projectPath: String) : ElasticModule()