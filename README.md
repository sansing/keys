# Keys

Keys is a general purpose easy-to-use security framework for applications written in clojure.

## Installation
The latest release is `0.1.0` and can be installed by running the following command:

    $ lein install

## Getting started
Keys uses a couple of concepts, which are `keys` and `subjects`.

In keys a `key` is a simple vector which represent a certain permission a `subject` can have or is required to have.
An example of an key is: `[:object 1 :read]`, keys can represent anything, in this case it represents a read action on
an object with id 1. But it can also be used to indicate a role like `[:admin]`.

A `subject` is a authenticatable/authorizable entity, it can be a user or a client system authenticating against your
application. The subject has a number of permissions, also called `keys`. Besides keys the subject is a standard format
to represent a authenticatable in your system, and thus the subject also consists of general information about the subject.

The subject format is represented as follows:
```clj
{
 :id   ; ID of the subject
 :keys ; A collection of keys which specify which permissions this subject has
 :info ; Custom information, this can be anything including nil
}
```

### Securing Expressions
A great feature of Keys is that you can secure any arbitrary expression. To secure a expression you should use the
`secured` macro, which accepts a key expression and the to-be-secured expression as it's body. The `secured` macro makes
use of the current subject context to determine if the expression should be evaluated.

Example usage of the secured macro:

```clj
(use 'keys.core)

(secured [:admin]
    (println "This is a admin-only println"))
```

The `secured` macro returns the value of the evaluated body, or throws one of the following slingshot exceptions:

* {:type :no-subject},  The current subject has not been set
* {:type :invalid-key :key <k>}, The key provided is not a valid key (it is not a vector)
* {:type :unauthorized :required <k> :subject <current-subject>}, The current subject is not authorized to perform the expression

### Setting the subject
For using the `secured` macro the current subject needs to be set. To do that you need to use the `with-subject` macro.

```clj
(use 'keys.core)
(use 'keys.subject)

(with-subject {:id 1 :keys [[:user] [:admin]] :info nil}
    (secured [:admin] (println "This is a admin-only println")))
```

The `with-subject` macro uses a thread-local binding, and can thus be set anywhere in the call-stack:

```clj
(use 'keys.core)
(use 'keys.subject)

(defn admin-fn []
    (secured [:admin]
        (println "This is a admin-only println"))


(with-subject {:id 1 :keys [[:user] [:admin]] :info nil}
    (admin-fn))
```

# License

Copyright © 2014 Stefan Ansing

Distributed under the Eclipse Public License.