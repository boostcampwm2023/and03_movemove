import { Type, applyDecorators } from '@nestjs/common';
import { ApiExtraModels, ApiResponse, getSchemaPath } from '@nestjs/swagger';
import { BaseException } from 'src/exceptions/base.exception';

export const ApiFailResponse = <TException extends Type<BaseException>>(
  description: string,
  models: TException[],
) => {
  const examples = models.reduce((acc, model) => {
    const modelInstance = new model();
    return Object.assign(acc, {
      [modelInstance.errorCode]: {
        value: {
          statusCode: modelInstance.errorCode,
          message: modelInstance.message,
        },
      },
    });
  }, {});
  const modelInstance = new models[0]();

  return applyDecorators(
    ApiExtraModels(BaseException),
    ApiResponse({
      status: modelInstance.getStatus(),
      description,
      content: {
        'application/json': {
          schema: { $ref: getSchemaPath(BaseException) },

          examples,
        },
      },
    }),
  );
};
