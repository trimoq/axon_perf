# Neuron and Synapse
Two projects to analyze Axon performance.
The code here is used in several blog posts:
- [That one time I had a Postgres cluster on my veranda](https://example.com)

## Synapse
Synapse is used to let Axon create lots of Aggregates with one event each.
The Aggregates are created by issuing commands, executing CommandHandlers, which in turn emit Events.

The Synapse project contains additional code to recreate certain database states, like aggregates with a large, fixed amount of events already stored in them for an aggregate update benchmark later on. 

## Neuron
Neuron is used to execute a similar query on a non-jvm language.
The query is slightly different, simply to keep it small.