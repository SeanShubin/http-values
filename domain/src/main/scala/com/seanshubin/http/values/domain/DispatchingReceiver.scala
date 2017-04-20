package com.seanshubin.http.values.domain

class DispatchingReceiver(routes: Seq[Route]) extends Receiver {
  SequenceUtil.searchForFirstDuplicate(routes.map(_.name)) match {
    case Some(name) => throw new RuntimeException(s"Duplicate route '$name'")
    case None =>
  }

  override def receive(request: RequestValue): ResponseValue = {
    def isMatchingRoute(route: Route) = route.accept(request)
    val matchingRoutes = routes.filter(isMatchingRoute)
    if (matchingRoutes.size == 1) matchingRoutes.head.receiver.receive(request)
    else if (matchingRoutes.isEmpty) {
      val routeNames = routes.map(_.name).mkString(", ")
      throw new RuntimeException(s"No receivers matched $request: $routeNames")
    } else {
      val routeNames = matchingRoutes.map(_.name).mkString(", ")
      throw new RuntimeException(s"Multiple receivers matched $request: $routeNames")
    }
  }
}
