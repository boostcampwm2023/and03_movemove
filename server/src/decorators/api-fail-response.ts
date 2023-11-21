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
      [modelInstance.statusCode]: {
        description: modelInstance.description,
        value: {
          statusCode: modelInstance.statusCode,
          message: modelInstance.message,
        },
      },
    });
  }, {});
  const modelInstance = new models[0]();

  return applyDecorators(
    ApiExtraModels(...(models instanceof Array ? models : [models])),
    ApiResponse({
      status: modelInstance.getStatus(),
      description,
      content: {
        'application/json': {
          schema: { $ref: getSchemaPath(models[0]) },

          examples,
        },
      },
    }),
  );
};
