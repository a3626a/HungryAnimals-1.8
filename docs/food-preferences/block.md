# Block

## Introduction

`'food_preferences/block/{modid}/{animal}.json'` contains list of block\_states which provides `"nutrient"` and `"stomach"`. The animal eats placed blocks which are listed.

## Grammar

{% code title="food\_preferences/block/{modid}/{animal}.json" %}
```text
[
  {
    "block": block_state,
    "nutrient": double,
    "stomach": double
  },
  ...
]
```
{% endcode %}

{% hint style="info" %}
For _block state_, follow this page

{% page-ref page="../basics/miscellaneous.md" %}
{% endhint %}

