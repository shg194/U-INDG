# An Efficient Approach Towards Index Structures for Skyline Queries

## Introduce
    
  Given a multidimensional dataset, a skyline query aims to find all objects that are not dominated by any other object 
  in the dataset. If an object 𝐴 is not worse than 𝐴′ in any dimension, and there is at least one dimension in which 𝐴 
  is better than 𝐴′, we say that 𝐴 dominates 𝐴′. The skyline query represents a typical multi-objective optimization problem 
  and is widely applied in fields such as data analysis, decision support, intelligent recommendation, network optimization, 
  and healthcare.

  Many index-based algorithms focus on reducing dominance tests using additional data structures but ignore further 
  exploration of the original structure for data storage. After all, the cost of build-ing additional data structures is very
  high. In fact, there is no necessity for additional data structures to reduce the dominance tests in skyline queries for 
  most index-based structures. Because in most index structures, data is organized in a certain order. 

  **U-INDG** approach is an index-oriented framework for skyline queries, that can significantly reduce the cost of dominance 
  tests. All of the code necessary to repeat experiments from the paper are available in this suite. Extensive experimental 
  results demonstrate that **U-INDG** shows significant improvements when extended to multiple index structures, and the 
  query efficiency across various datasets outperforms that of state-of-the-art algorithms.

------------------------------------
## Algorithms

The following algorithms have been implemented in **U-INDG**:

* **BBS** : Located in [src/myshgs/Others/BBS](src/myshgs/Others/BBS).
  BBS of Tao is a representative algorithm, which can still maintain high query efficiency in low-dimensional space.

* **MBR-Oriented** : Located in [src/myshgs/Others/MBR_Oriented](src/myshgs/Others/MBR_Oriented).
  It is a simplification of Hybrid to demonstrate control flow.

[//]: # (* **PSkyline** [3]: Located in [src/pskyline]&#40;src/pskyline&#41;.)

[//]: # (  It was the previous state-of-the-art multi-core algorithm, based)

[//]: # (  on a divide-and-conquer paradigm.)

[//]: # ()
[//]: # (* **BSkyTree** [4]: Located in [src/bskytree]&#40;src/bskytree&#41;.)

[//]: # (  It is the state-of-the-art sequential algorithm, based on a)

[//]: # (  quad-tree partitioning of the data and memoisation of point-to-point)

[//]: # (  relationships.)

[//]: # (All four algorithms are implementations of the common interface defined in)

[//]: # ([common/skyline_i.h]&#40;common/skyline_i.h&#41; and use common dominance tests from  )

[//]: # ([common/common.h]&#40;common/common.h&#41; and [common/dt_avx.h]&#40;common/dt_avx.h&#41;)

[//]: # (&#40;the latter when vectorisation is enabled&#41;.)

------------------------------------