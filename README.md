# Http Values
Http without mutable state

## Rationale
Java's servlet api is designed around side effects on the response.
It has a signature like this:

    javax.servlet.Servlet
    public void service(ServletRequest req,
                        ServletResponse res)
                 throws ServletException,
                        java.io.IOException

I wanted to treat the http request/response process as a function, like so

    // Client
    trait Sender {
      def send(request: RequestValue): ResponseValue
    }

    // Server
    trait Receiver {
      def receive(request: RequestValue): ResponseValue
    }

This allows implementors to test pure functions rather than dealing with streams.

There is also a good bit of support for handling dispatch in a testable way.
A server can compose a bunch of named receivers together, an be confident that any path collisions will fail immediately with a sensible error message, rather than having an arbitrary receiver pick up the request.  

## Limitations
- The request and response values are reified immediately.
  This is usually not a problem unless your application typically ignores large portions of the request or response and thus could be optimized by waiting until they needed to be reified.
- By design, this library does not handle streaming.
  If you need streaming this is the wrong tool for the job.
