import { IgnoreSymbol } from 'src/transform.interceptor';

export function IgnoreInterceptor() {
  return function (
    target,
    propertyKey: string,
    descriptor: PropertyDescriptor,
  ) {
    // eslint-disable-next-line no-param-reassign
    descriptor.value[IgnoreSymbol] = true;
  };
}
