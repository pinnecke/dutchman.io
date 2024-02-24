package com.mygdx.game.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val YAML_MAPPER = ObjectMapper(YAMLFactory()).registerKotlinModule()